# Aplicatie Bancara — PAOJ Etapa II



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
