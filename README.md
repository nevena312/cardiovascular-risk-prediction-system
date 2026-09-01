# Cardiovascular Risk App

Lokalna akademska web aplikacija za klasifikaciju koronarne bolesti pomoću već treniranog DeepNetts neuralnog modela.

Ovaj sistem je razvijen u istraživačke i akademske svrhe. Rezultat predstavlja izlaz prediktivnog modela i ne predstavlja medicinsku dijagnozu niti zamenu za procenu zdravstvenog stručnjaka.

## Arhitektura

- Backend: Java 17, Spring Boot, Spring Web, Spring Data JPA, PostgreSQL, DeepNetts
- Frontend: React, Vite, JavaScript, Recharts
- Model: učitava se iz `models/deepnetts_cardiovascular_model.dnet`
- Skaliranje starosti: učitava se iz `models/age_scaler.txt`

Spring Boot aplikacija ne trenira model pri pokretanju i ne učitava trening skup za normalnu predikciju. `Main.java` ostaje kao reproduktivni razvojni/trening tok, dok je produkcioni ulaz `CardiovascularApplication.java`.

## Model

Model koristi tačno sedam ulaza ovim redosledom:

1. `sex`
2. `age`
3. `hyperlipidemia`
4. `smoker`
5. `diabetes`
6. `obesity`
7. `hypertension`

Kodiranje pola:

- `1` = muško
- `0` = žensko

Starost se standardizuje pomoću sačuvanih trening parametara:

```text
standardizedAge = (rawAge - trainingAgeMean) / trainingAgeStandardDeviation
```

Prag klasifikacije je `0.5`.

U model se nikada ne prosleđuju `id`, `patientCode`, ime, prezime, vreme, `original_index`, `bypass_count` ili druga identifikaciona polja.

## Lokalno objašnjenje

Aplikacija koristi postojeću klasu `DeepNettsExplainer` i metodologiju lokalne one-feature-at-a-time counterfactual sensitivity analize. Ovo nije SHAP.

Za svaku procenu čuva se svih sedam lokalnih doprinosa u bazi, tako da istorijska procena prikazuje objašnjenje generisano u trenutku procene.

## Baza

PostgreSQL baza služi za:

- pacijente
- istoriju procena
- istorijske izlaze modela
- istorijska lokalna objašnjenja
- statistiku kontrolne table

Baza ne menja trenirani model i nove procene nikada ne pokreću retreniranje.

Primer kreiranja lokalne baze:

```sql
CREATE DATABASE cardiovascular_risk;
```

Konfiguracija se zadaje promenljivama okruženja:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/cardiovascular_risk"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your-local-password"
$env:MODEL_PATH="models/deepnetts_cardiovascular_model.dnet"
$env:SCALER_PATH="models/age_scaler.txt"
$env:CORS_ALLOWED_ORIGINS="http://localhost:5173"
```

Podrazumevani backend port je `8080`.

## Pokretanje backend-a

```powershell
mvn spring-boot:run
```

API je dostupan na:

```text
http://localhost:8080/api
```

## Pokretanje frontend-a

```powershell
cd frontend
npm install
npm run dev
```

Vite je dostupan na:

```text
http://localhost:5173
```

Za produkcioni build:

```powershell
cd frontend
npm run build
```

## API pregled

Pacijenti:

- `POST /api/patients`
- `GET /api/patients`
- `GET /api/patients?search=PAT-000001`
- `GET /api/patients/{id}`
- `GET /api/patients/{id}/assessments`

Procene:

- `POST /api/patients/{patientId}/assessments`
- `GET /api/assessments`
- `GET /api/assessments/recent`
- `GET /api/assessments/{id}`

Kontrolna tabla:

- `GET /api/dashboard`

## Stranice aplikacije

- Kontrolna tabla
- Pacijenti
- Detalji pacijenta
- Nova procena
- Detalji istorijske procene

## Verifikacija

Backend testovi proveravaju sačuvani scaler, učitavanje DeepNetts modela, poznati validacioni slučaj i čuvanje istorijskih doprinosa.

Poznati test pacijent:

```text
sex=1
age=51
hyperlipidemia=1
smoker=0
diabetes=1
obesity=1
hypertension=1
```

Očekivana verovatnoća je približno `0.881591`, a klasa `1`.

Pokretanje testova:

```powershell
mvn test
```

## Napomena o npm audit-u

`npm audit --omit=dev` prijavljuje probleme u Vite/React Router lancu i predlaže prelazak na veće major verzije. Te nadogradnje nisu automatski primenjene da se ne uvede breaking-change rizik u demonstracionu aplikaciju. Frontend produkcioni build je uspešno proveren.
