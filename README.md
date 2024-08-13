## Raamatute Laenutamise Rakendus

Loo crowdsource-ingul põhinev raamatute laenutamise rakendus, arvestades järgmisi soovitusi:

### Tehnilised nõuded
- **Backend:** Java 17+, Spring Boot
- **Frontend:** Angular
- **Baas:** PostgreSQL
- **Lahendus:** Dockeriseeritud
- **REST API dokumentatsioon:** Kasutada tööriistu nagu Swagger, mis peab vastama OpenAPI spetsifikatsioonile.
- **Kood:** Kattega testidega

### Rakenduse Ülevaade
Java rakendus, mis võimaldab luua kasutajaid, mille abil saab teha järgmisi toiminguid kahe rolli põhjal: raamatute laenutamine ja raamatute väljalaenamine. Vaikeseade raamatute laenutusel võiks olla 4 nädalat.

#### Raamatute Laenutamine
- Kasutaja saab otsida raamatut
- Kasutaja saab raamatu broneerida ning broneeringut tühistada
- Kasutaja saab raamatu märkida kättesaanuks ja tagastatuks

#### Raamatute Väljalaenamine
- Kasutaja saab lisada ja eemaldada raamatuid väljalaenamiseks
- Kasutaja saab tema raamatule tehtud broneeringut tühistada
- Kasutaja saab raamatu märkida üleantuks ja tagastatuks