# HoRecaPilot

## Cos'è
Backend Spring Boot per il controllo della redditività operativa di ristoranti (HoReCa):
personale, turni, vendite, menu/ricette, food cost e margini in un unico posto.
Il KPI centrale del prodotto è il **prime cost = food cost + labor cost**.

MVP: tutto inserito **manualmente** tramite le API REST del progetto.
Nessuna integrazione esterna in questa fase (no fatturazione/Aruba, no parsing PDF del menu).

## Stack
- Java 21, Spring Boot 4.1.0 (Spring Framework 7), build Maven
- PostgreSQL (istanza locale in sviluppo)
- Spring Web, Spring Data JPA, Bean Validation, Flyway
- Base package: `com.hamid.horecapilot`

## Architettura
Monolite modulare, package **per dominio** (non per layer tecnico):

```
com.hamid.horecapilot
├── staff       (Employee, Shift)
├── sales       (DailySales)
├── menu        (Ingredient, MenuItem, RecipeLine)
├── analytics   (motore di calcolo dei KPI — nessuna entity)
└── common      (config, gestione eccezioni globale, classi condivise)
```

Ogni package di dominio contiene le proprie entity, repository, service, dto e controller.

## Metodo di lavoro: slice verticali
Si costruisce **una funzionalità end-to-end alla volta**, in quest'ordine:
migration → entity → repository → dto → service → controller → test.
Si verifica che giri, si committa, si passa alla slice successiva.
NON costruire prima tutte le entity, poi tutti i repository, ecc.
La slice **Employee** è il "golden template": le slice successive replicano la stessa struttura.

## Regole non negoziabili
1. **Soldi = `BigDecimal`** in Java e **`NUMERIC`** in Postgres. Mai `double`/`float`.
   Nei calcoli usare scale e `RoundingMode.HALF_UP` espliciti.
2. **Lo schema lo possiede Flyway.** Migration versionate in
   `src/main/resources/db/migration` (`V1__...`, `V2__...`).
   Hibernate solo in verifica: `spring.jpa.hibernate.ddl-auto=validate`.
   Mai `update`/`create`. Non modificare una migration già committata: crearne una nuova.
3. **I service restituiscono DTO, mai entity.** DTO separati per input (`...Request`)
   e output (`...Response`). I DTO sono `record`. Validazione Bean Validation (`@Valid`)
   sui `...Request` nel controller, non sull'entity.
4. **Ogni tabella ha `restaurant_id`** (anche se per ora c'è un solo ristorante).
   Per l'MVP è fisso a `1`. Aggiungerla ora costa zero, retrofittarla dopo è un incubo.
5. **Gestione eccezioni centralizzata** in `common` con `@RestControllerAdvice`:
   404 per entità non trovata, 400 per validazione, corpo errore consistente.
6. **Niente logica di business nei controller**: solo orchestrazione di input/output.
   `@Transactional` sui metodi di scrittura del service.
7. **Test sulla matematica dei soldi**: i calcoli (costo turno, KPI, food cost) hanno
   unit test veri. Il CRUD può avere test leggeri.

## Convenzioni REST
- Prefisso `/api`, sostantivi plurali (`/api/employees`).
- Status code corretti: 201 + header `Location` alla creazione, 200, 204, 400, 404.
- Mapping entity↔DTO esplicito (metodo privato nel service o piccolo mapper dedicato).

## Confini dell'MVP (cosa NON fare ora)
- No Spring Security / autenticazione (si aggiunge dopo).
- No integrazione fatturazione/Aruba, no parsing PDF del menu.
- No tabella `daily_metrics` precalcolata: i KPI si calcolano **a lettura**,
  aggregando al volo. Il read model si introduce solo quando i volumi lo richiederanno.
- No sales mix per piatto (`SaleLine`): nell'MVP il food cost è solo a livello di
  singolo piatto (menu engineering); il prime cost giornaliero si ferma al labor cost.

## Modello di dominio (MVP)
- **staff** — `Employee`(nome, ruolo, costoOrarioAziendale, attivo),
  `Shift`(employee, data, oraInizio, oraFine, ruolo)
- **sales** — `DailySales`(data [unica], fatturato, coperti)
- **menu** — `Ingredient`(nome, unita, costoUnitario),
  `MenuItem`(nome, prezzoVendita, categoria),
  `RecipeLine`(menuItem, ingredient, quantita)
- **analytics** — nessuna entity, solo service di calcolo

### Note di dominio
- `costoOrarioAziendale` è il costo orario **carico** (lordo + contributi + ratei di
  tredicesima/quattordicesima + TFR), NON la paga lorda. Per l'MVP è un singolo valore
  corrente sul dipendente (i turni storici verranno ricalcolati col valore aggiornato:
  limite accettato in questa fase).
- I dipendenti si **disattivano** (`attivo=false`), non si cancellano fisicamente:
  servono a preservare lo storico dei turni e dei costi.
- I turni possono **scavalcare la mezzanotte** (chiusura alle 02:00): gestire
  esplicitamente il calcolo delle ore in questo caso.
