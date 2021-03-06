# BBdebet
BBdebet er en app for å behandle transaksjoner i debetboka. Det er en digital utgave av en gammeldags kriteliste, men uten mulighet for at folk regner feil. Utviklet med tanke på Biørneblæs' selvbetjente snacks- og kaffehjørne.


### Versjon 2
BBdebet2 er en helt ny versjon av BBdebet, bygd på nytt fra bunnen av.


## Installasjonsguide
For å installere BBdebet 2 flytter du deg til en passende mappe (feks Downloads) og skriver følgende i terminalen:
``` bash
$ git clone https://github.com/mathialo/bb-debet2.git
$ cd bb-debet2
$ bash install.sh
```
Da vil siste versjon bli lastet ned og installert. Har du allerede en lokal versjon av git-branchen og vil oppdatere kan du pulle i stedet for å klone:
``` bash
$ git pull origin master
$ bash install.sh
```
Installasjonsskriptet vil legge programmet inn i `/usr/local/share/bbdebet2/`, og legge symbolske linker i `/usr/local/bin/` slik at programmet er tilgjengelig fra terminalen. Det vil også legge til ikon i startmenyen.

Programmet vil bruke `~/.bbdebet2/` til datalagring. Denne mappa vil bli initialisert av installasjonsskriptet.



## Versjoner
Under ligger en liste med alle utgivelsesnotater siden versjon 1.0.

### 2.2 (under utvikling)
 - Støtte for Mac OS
 - Regnskapsføring: Track utlegg, innskudd, avanse ++ fra BBDebet, og generer regnskap på slutten av semesteret automatisk.


### 2.1
 - Riktig lisenshåndtering for dependencies
 - Shipper nå med OpenJDK og OpenJFX for å gjøre installering enklere. Bruker trenger ikke lenger ha en installasjon av java på systemet. Vi er heller ikke avhengige av OracleJDK som gjør lisensendringen i versjon 11 uproblematisk for bruken av BBDebet2.
 - Oppdattert installeringsskript


### 2.0
 - Total omskriving av hele kodebasen. Store forbedringer for stabilitet, kjøretid og vedlikedholdbarhet.
 - Nytt og forbedret lagersystem. Ulike varer av samme type kan nå ha ulik pris. Mulighet for å beregne avanse per vare.
 - Totalt nytt brukergrensesnitt for administrator
 - Semiautomatisk innskuddshåndtering
 - Flere nye funksjoner slik som mulighet for EULA og plugins


### 1.8
 - Ny frond-end for eksportering og tilbakestilling.
 - Visualisering av svinn og innskudd
 - Favoritter er nå sortert fra mest til minst kjøpt
 - Kan tilbakestille debetboka til et backuppunkt fra programmet
 - Sending av mail til administrator
 - Glassbruker
 - Reply-To i mail
 - Bedre handlelistegenerering
 - Flere feil rettet opp i installeringsskript


### 1.7
 - Finere innstillinger
 - Forbedret favoritter. Annet-ting kan nå legges til fra favoritter.
 - La til mulighet for å endre minimumsbeholdning og hissighetsparameter i handlelistegenereringen
 - Mulighet for å automatisk sende epost til brukere som går i minus
 - Velkomstmail til nye brukere
 - Mer robust lagring av passord
 - Eksportering av data
 - Noen små forberdinger
 - Noen småfeil rettet opp


### 1.6

 - Forbedret handlelistegenerering
 - La til svinnkorrigering med loggføring
 - Loggføring av utlegg
 - Loggføring av innskudd
 - Mulighet for autobackup/-innlasting fra UiO-server
 - La til enkel splash-skjerm siden startupen har blitt småtreg
 - Administrator kan sende bekreftelsesmail når brukere setter inn penger
 - Administrator kan nå endre hvor fort brukere automatisk logges ut


### 1.5

 - Administrator kan nå sende mail til alle i minus og mase om penger
 - Administrator kan endre innstillinger i programmet, blant annet expært-passort og mail
 - Autogenerering av handlelister for administrator
 - Brukere får nå opp en liste med sine mest kjøpte varer
 - Noen småfeil rettet opp


### 1.4

 - Brukere blir nå varslet om at de blir autologget ut
 - Flere småfeil rettet opp


### 1.3

 - Flere småfeil rettet opp
 - Flere småforbedringer bak kulissene


### 1.2

 - Liste over brukers transaksjoner på kjøpsside
 - Flere småfeil rettet opp


### 1.1

 - Kontonummer på forside
 - Lagring skjer hvert 3. minutt, og skriver over tidligere filer
 - Lagringshistorikk hver time
 - Flere småfeil rettet opp


### 1.0
 - Første stabile versjon.

