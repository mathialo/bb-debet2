# BBdebet
BBdebet er en app for å behandle transaksjoner i debetboka. Det er en digital utgave av en gammeldags kriteliste, men uten mulighet for at folk regner feil. Utviklet med tanke på Biørneblæs' selvbetjente snacks- og kaffehjørne. 


# Versjon 2
BBdebet2 er en helt ny versjon av BBdebet. Foreløpig er den ikke ferdig, så bruk versjon 1.x enn så lenge. 

## Roadmap til første release av 2.x
Lister over ferdig funksjonalitet, og funksjonalitet som må på plass før neste utgivelse:

### Ferdig
 - Lagerfunksjonalitet
 - Logging
 - Salgshistorikk
 - Kjøpsfunksjonalitet
 - Overføring av saldo mellom brukere
 - Generering av favoritter
 - Legge til varer

### Må fikses
 - Endre varer
 - Føring av svinn
 - Legge til brukere
 - Endre brukere
 - Legge til saldo på brukere
 - Epostspamming
 - Lagring av instillinger
 - Automatisk mellomlagring
 - Backups (snapshots av hele systemet som lagres separat)
 - Gjenoppretting fra sikkerhetskopier

### Bør fikses
 - Handlelistegenerering
 - Automatisk statistikkgenerering
 - Glassbruker
 - Automatisk utlogging (med varsel)
 - Backup til eksern server
 - Eksportering og tilbakestilling av data
 - Visualisering av svinn og innskudd
 - Brukere kan be om refundering

# Versjoner
Under ligger en liste med alle utgivelsesnotater siden versjon 1.0.

## 2.0 (pre-alpha)
 - Total omskriving av hele kodebasen for økt stabilitet og vedlikedholdbarhet. Bedre inndeling av kode, og GUI skrevet i FXML. 
 - Nytt lagersystem. Ulike varer av samme type kan nå ha ulik pris. 


## 1.8
 - Ny frond-end for eksportering og tilbakestilling.
 - Visualisering av svinn og innskudd
 - Favoritter er nå sortert fra mest til minst kjøpt
 - Kan tilbakestille debetboka til et backuppunkt fra programmet
 - Sending av mail til Choostholdsexpært
 - Glassbruker
 - Reply-To i mail
 - Bedre handlelistegenerering
 - Flere feil rettet opp i innstalleringsskript


## 1.7
 - Finere innstillinger
 - Forbedret favoritter. Annet-ting kan nå legges til fra favoritter.
 - La til mulighet for å endre minimumsbeholdning og hissighetsparameter i handlelistegenereringen
 - Mulighet for å automatisk sende epost til brukere som går i minus
 - Velkomstmail til nye brukere
 - Mer robust lagring av passord
 - Eksportering av data
 - Noen små forberdinger
 - Noen småfeil rettet opp


## 1.6

 - Forbedret handlelistegenerering
 - La til svinnkorrigering med loggføring
 - Loggføring av utlegg
 - Loggføring av innskudd
 - Mulighet for autobackup/-innlasting fra UiO-server
 - La til enkel splash-skjerm siden startupen har blitt småtreg
 - Choostholdsexpært kan sende bekreftelsesmail når brukere setter inn penger
 - Choostholdsexpært kan nå endre hvor fort brukere automatisk logges ut


## 1.5

 - Choostholdsexpært kan nå sende mail til alle i minus og mase om penger
 - Choostholdsexpært kan endre innstillinger i programmet, blant annet expært-passort og mail
 - Autogenerering av handlelister for Choostholdsexpært
 - Brukere får nå opp en liste med sine mest kjøpte varer
 - Noen småfeil rettet opp


## 1.4

 - Brukere blir nå varslet om at de blir autologget ut
 - Flere småfeil rettet opp


## 1.3

 - Flere småfeil rettet opp
 - Flere småforbedringer bak kulissene


## 1.2

 - Liste over brukers transaksjoner på kjøpsside
 - Flere småfeil rettet opp


## 1.1

 - Kontonummer på forside
 - Lagring skjer hvert 3. minutt, og skriver over tidligere filer
 - Lagringshistorikk hver time
 - Flere småfeil rettet opp


## 1.0
 - Første stabile versjon.

