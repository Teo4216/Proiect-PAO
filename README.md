1.1 - Lista de actiuni si interogari in sistem
	1.	Inregistrarea unui client nou in sistem.
	2.	Deschiderea unui cont bancar de tip curent pentru un client.
	3.	Deschiderea unui cont de economii (cu rata de dobanda).
	4.	Depunerea de numerar intr-un cont specific (dupa IBAN).
	5.	Retragerea de numerar din cont (cu verificare de fonduri insuficiente).
	6.	Efectuarea unui transfer bancar intre doua conturi.
	7.	Generarea si afisarea unui istoric de tranzactii imutabile.
	8.	Listarea tuturor clientilor ordonati alfabetic.
	9.	Cautarea rapida a unui cont folosind IBAN-ul.
	10.	Calcularea banilor totali detinuti de un client in toate conturile sale.
1.2 - Tipuri de obiecte (Modele)
	1.	Persoana (clasa abstracta)
	2.	Client (mosteneste Persoana)
	3.	Cont (clasa abstracta)
	4.	ContCurent (mosteneste Cont)
	5.	ContEconomii (mosteneste Cont)
	6.	Tranzactie (clasa imutabila)
	7.	Adresa
	8.	CardBancar
concepte oop utilizate:
1.Singleton Pattern: folosit pentru ClientService si ContService.
2.Colectii: List (istoric tranzactii), Set sortat cu Comparator (clienti) si Map (cautare rapida conturi dupa IBAN).
3.Exceptii custom: ClientInexistentException si FonduriInsuficienteException.
4.Abstractizare si Mostenire: ierarhii pe 2 niveluri pentru Persoana/Client si Cont/ContCurent/ContEconomii.
