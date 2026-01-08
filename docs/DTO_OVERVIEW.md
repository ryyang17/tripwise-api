# Backend DTO Overzicht - TripWise API

Dit document geeft een overzicht van alle DTOs die zijn toegevoegd/geüpdatet om te aligneren met de frontend specificatie.

## Structuur

```
dto/
├── request/
│   ├── CreateTripRequest.java
│   ├── UpdateTripRequest.java
│   ├── CreateParticipantRequest.java
│   ├── CreateActivityRequest.java
│   ├── UpdateActivityRequest.java
│   ├── CreateBudgetRequest.java
│   ├── UpdateBudgetRequest.java
│   ├── CreateExpenseRequest.java
│   ├── UpdateExpenseRequest.java
│   ├── SharedExpenseRequest.java
│   ├── CreateCategoryRequest.java
│   ├── UpdateCategoryRequest.java
│   ├── CreateUserRequest.java
│   └── UpdateUserRequest.java
└── response/
    ├── TripResponse.java
    ├── TripParticipantResponse.java
    ├── ActivityResponse.java
    ├── BudgetResponse.java
    ├── ExpenseResponse.java
    ├── SharedExpenseResponse.java
    ├── ExpenseCategoryResponse.java
    ├── UserResponse.java
    ├── ExchangeRateResponse.java
    └── ErrorResponse.java
```

## Belangrijke Frontend Aligneringen

### 1. Trip DTOs
- **CreateTripRequest / UpdateTripRequest**
  - Bevat `destinationCoords` nested object met `lat` en `lng`
  - `transportation` met pattern validatie: plane|car|bus|train
  - Datums als `LocalDate` met `@JsonFormat(pattern = "yyyy-MM-dd")`

- **TripResponse**
  - Bevat berekend veld `isActive` (readonly, door backend bepaald)
  - `createdDate` als ISO datetime

### 2. Participant DTOs
- **CreateParticipantRequest**
  - `userId`, `userName`, `userEmail`, `role`, `isOwner`
  - Minimale validatie voor flexibiliteit

- **TripParticipantResponse**
  - Bevat ZOWEL `participantId` als `id` voor backwards compatibility
  - `participantId` is de canonieke naam zoals frontend verwacht
  - Beide worden in sync gehouden via setters

### 3. Activity DTOs
- **CreateActivityRequest / UpdateActivityRequest**
  - `date` en `time` kunnen `null` zijn (frontend stuurt lege strings als null)
  - `time` als String in HH:MM formaat
  - `locationDetails` nested object met address, latitude, longitude, placeId
  - `estimatedCost` als `BigDecimal`

- **ActivityResponse**
  - Zelfde structuur, plus `createdDate`

### 4. Budget DTOs
- **CreateBudgetRequest / UpdateBudgetRequest**
  - Simpel: alleen `totalBudget` en `currency`
  - Currency validatie: 3-letter ISO code

- **BudgetResponse**
  - Bevat optionele `trip` (TripSummary nested object)
  - Bevat optionele `categories` array met spending per category
  - Flexibel voor verschillende use cases

### 5. Expense DTOs
- **CreateExpenseRequest / UpdateExpenseRequest**
  - **BELANGRIJK**: Accepteert embedded `category` object (zoals frontend stuurt)
  - Ook `categoryId` field voor alternatieve aanpak
  - Controllers moeten checken: als `category != null`, gebruik `category.getCategoryId()`
  - Bedragen als `BigDecimal` voor precisie
  - Shared expense fields: `paidById`, `splitWith`, `splitEvenly`, `customSplits`

- **ExpenseResponse**
  - Bevat volledige `ExpenseCategory` object (niet alleen ID)
  - `amount` = geconverteerd naar user's currency
  - `originalAmount` en `currency` = originele waarden
  - Alle shared expense velden

### 6. Shared Expense DTOs
- **SharedExpenseRequest** (nu in request package)
  - `paidByParticipantId` en `splitWithParticipantIds` (participant IDs, niet user IDs!)
  - `customSplits` met String keys (participantId als string -> BigDecimal amount)
  - Validatie met Jakarta Validation annotations

- **SharedExpenseResponse**
  - `splits` map: participantId (Integer) -> amount (BigDecimal)
  - Optionele `paidByParticipantName` voor UI display

### 7. Category DTOs
- **CreateCategoryRequest / UpdateCategoryRequest**
  - `categoryName`, `categoryDescription`, `isActive`

- **ExpenseCategoryResponse**
  - Standaard response met alle velden + `createdDate`

### 8. User DTOs
- **CreateUserRequest**
  - Alle velden inclusief `password` (vereist bij create)
  - Validatie: email format, max lengths

- **UpdateUserRequest**
  - Zelfde velden maar zonder `password` (apart endpoint voor password change)
  
- **UserResponse**
  - Geen `password` field (security!)
  - `userId` in plaats van `id`
  - Optionele `joinDate` voor display (afgeleid van `createdDate`)

### 9. Utility DTOs
- **ExchangeRateResponse**
  - Voor optionele `/api/currency/exchange-rate` endpoint
  - `from`, `to`, `rate`, `lastUpdated`

- **ErrorResponse**
  - Gestandaardiseerde error response
  - `message`, `details`, `timestamp`, `status`, `path`
  - Gebruik in exception handlers voor consistente foutmeldingen

## Validatie

Alle request DTOs gebruiken Jakarta Validation annotations:
- `@NotNull`, `@NotBlank`, `@NotEmpty`
- `@Positive` voor bedragen
- `@Email` voor email velden
- `@Size` voor length constraints
- `@Pattern` voor regex validatie (bijv. transportation)
- `@JsonFormat` voor datum/tijd parsing

## Datum/Tijd Conventies

- **Datum** (LocalDate): `@JsonFormat(pattern = "yyyy-MM-dd")`
  - Gebruik voor: startDate, endDate, expenseDate, activity.date
  
- **Datum/Tijd** (LocalDateTime): `@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")`
  - Gebruik voor: createdDate, joinedDate, lastUpdated
  
- **Tijd** (String): HH:MM formaat
  - Gebruik voor: activity.time (kan null zijn)

## Type Mapping

### Frontend -> Backend
- `number` -> `Integer` of `Long` (voor IDs)
- `number` (money) -> `BigDecimal` (voor precisie)
- `string` (ISO date) -> `LocalDate`
- `string` (ISO datetime) -> `LocalDateTime`
- `boolean` -> `Boolean` (object, not primitive - kan null zijn)

### Nested Objects
- Frontend stuurt nested objects -> Backend accepteert nested DTOs
- Voorbeelden:
  - `destinationCoords: { lat, lng }` -> `DestinationCoords` class
  - `locationDetails: { address, latitude, longitude, placeId }` -> `LocationDetails` class
  - `category: { categoryId, categoryName, ... }` -> `ExpenseCategory` entity

## Aandachtspunten voor Controllers

1. **Trip.isActive**: Bereken dit veld bij response mapping (endDate >= today)

2. **Expense Category**: 
   ```java
   // In controller/service bij create/update expense:
   Long categoryId = request.getCategory() != null 
       ? request.getCategory().getCategoryId() 
       : request.getCategoryId();
   ```

3. **Participant IDs**: Frontend gebruikt `participantId`, response moet beide `id` en `participantId` bevatten

4. **Activity date/time**: Frontend kan lege strings sturen -> converteer naar null

5. **Currency**: Altijd 3-letter ISO codes (EUR, USD, GBP, etc.)

6. **Validation**: Gebruik `@Valid` annotation in controller methods



