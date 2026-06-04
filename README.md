# Aplicatie Bancara — PAOJ Etapa II

## Cum deschizi proiectul (CITESTE PRIMUL)

### Pas 1 — Descarca driverul SQLite JDBC
Descarca fisierul JAR de aici:
https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.3.0/sqlite-jdbc-3.45.3.0.jar

Salveaza-l in folderul `lib/` din proiect:
```
AplicatieBancara/
└── lib/
    └── sqlite-jdbc-3.45.3.0.jar   ← aici
```

### Pas 2 — Deschide in IntelliJ
1. **File → Open** → selecteaza folderul `AplicatieBancara`
2. Daca IntelliJ intreaba "Trust project?" → apasa **Trust Project**
3. Proiectul se deschide cu structura corecta — `Main.java` e vizibil in `src/com/pao/proiect/bank/`

### Pas 3 — Verifica configuratia (optional, daca nu compileaza)
- Click dreapta pe folderul `src/` → **Mark Directory as → Sources Root**
- **File → Project Structure → Modules → Dependencies** → verifica ca `sqlite-jdbc-3.45.3.0.jar` apare

### Pas 4 — Run
- Deschide `Main.java` → click pe butonul verde ▶ sau Shift+F10

---

## Structura proiect
```
AplicatieBancara/
├── src/
│   ├── db.properties          ← configurare conexiune JDBC
│   ├── schema.sql             ← schema baza de date (SQLite)
│   └── com/pao/proiect/bank/
│       ├── Main.java
│       ├── model/             ← Persoana, Client, Cont, ContCurent,
│       │                         ContEconomii, Tranzactie, CardBancar, Adresa
│       ├── service/           ← ClientService, ContService, AuditService
│       ├── repository/        ← Repository<T,ID>, ClientRepository,
│       │                         ContRepository, TranzactieRepository,
│       │                         CardBancarRepository
│       ├── exception/         ← ClientInexistentException,
│       │                         FonduriInsuficienteException
│       └── util/              ← DatabaseConnection, DatabaseInitializer
└── lib/
    └── sqlite-jdbc-3.45.3.0.jar   ← TREBUIE DESCARCAT MANUAL (vezi Pas 1)
```

## Cele 10 actiuni (toate logate in audit.csv)
| # | Actiune | Clasa |
|---|---------|-------|
| 1 | adauga_client | ClientService |
| 2 | deschide_cont_curent | ContService |
| 3 | deschide_cont_economii | ContService |
| 4 | depunere | ContService |
| 5 | retragere | ContService |
| 6 | transfer | ContService |
| 7 | afisare_tranzactii | ContService |
| 8 | listare_clienti | ClientService |
| 9 | cauta_cont | ContService |
| 10 | calcul_avere_client | ContService |

## Fisiere generate la rulare
- `paoj_banca.db` — baza de date SQLite (in directorul de lucru al proiectului)
- `audit.csv` — log actiuni (mod append, thread-safe cu ReentrantLock)

## Cerinte Etapa II bifate
- schema.sql: 4 tabele, PRIMARY KEY pe toate, 3 FOREIGN KEY, DROP TABLE IF EXISTS
- db.properties + DatabaseConnection Singleton
- Interfata generica Repository<T, ID>
- CRUD complet (save/findById/findAll/update/delete) pentru 4 entitati
- Toate SQL-urile cu PreparedStatement + try-with-resources
- Tranzactie JDBC explicita (transfer: 3 operatii atomice, commit/rollback)
- 3 interogari SQL cu JOIN
- AuditService CSV thread-safe (ReentrantLock, append), apelat din toate cele 10 actiuni
