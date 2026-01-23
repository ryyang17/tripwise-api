# Security Fixes Documentation

## Overzicht
Hieronder staan de belangrijkste beveiligingsproblemen en hoe we die hebben opgelost. Dit is aangepast aan de huidige JWT-authenticatie.

## Opgeloste problemen

### 1. **CORS-instellingen te ruim**
- **Probleem:** Alle websites konden verbinding maken met de API.
- **Oplossing:** Alleen opgegeven websites mogen verbinden. In productie is dit een vaste lijst.

### 2. **Te ruime toegangsregels**
- **Probleem:** Sommige API-routes waren voor iedereen toegankelijk.
- **Oplossing:** Alleen login, registratie, refresh, wachtwoord reset en documentatie/gezondheid zijn openbaar. Alles anders vereist inloggen.

### 3. **Foutmeldingen lekken informatie**
- **Probleem:** Foutmeldingen gaven te veel technische details.
- **Oplossing:** In productie zijn foutmeldingen kort en veilig. Alleen in test zie je meer details.

### 4. **Extra beveiligingsheaders**
- **Probleem:** Niet alle beveiligingsheaders waren ingesteld.
- **Oplossing:** Headers zoals CSP, X-Frame-Options en X-XSS-Protection zijn toegevoegd. Dit helpt tegen XSS en clickjacking.


