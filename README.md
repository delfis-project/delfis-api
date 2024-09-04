# api-user-sql
API utilizando a arquitetura MVC (sem views e com services) para manipulação do banco SQL. A aplicação foi desenvolvida para suportar o backend do aplicativo "Delfis!".
<br/>
Aplicativo: https://github.com/delfis-project/delfis-app
<br/>
Database: https://github.com/delfis-project/db-sql
<br/>
Serviço hospedado no Render: https://api-user-sql.onrender.com

## Tecnologias utilizadas
- Spring Boot: Framework principal para o desenvolvimento da API.
- PostgreSQL: Banco de dados relacional utilizado.
- Swagger: Ferramenta para documentação interativa da API.

## Entidades: resumo e endpoint
* AppUser: usuário base do app. /api/app-user  
* AppUserPowerup: tabela onde cada registro é uma posse de powerup pelo usuário. /api/app-user-powerup  
* AppUserTheme: tabela onde cada registro é uma posse de tema pelo usuário. /api/app-user-theme  
* Plan: planos de assinatura. /api/plan  
* PlanPayment: tabela onde cada registro é um pagamento de plano pelo usuário. /api/plan-payment  
* Powerup: powerups do jogo. /api/powerup  
* Streak: streaks de cada usuário. /api/streak  
* Theme: temas do jogo. /api/theme  
* UserRole: papel do usuário no sistema. /api/user-role  

## Métodos comuns
* GET /get-all: traz todos os registros
* POST /insert: insere um registro com base num objeto JSON do body da requisição
* DELETE /delete/{id}: deleta um registro pelo id dele via PathVariable
* PUT /update/{id}: atualiza um registro por inteiro com base num objeto JSON do body da requisição por id
* PATCH /update/{id}: atualiza um registro parcialmente com base num objeto JSON do body da requisição por id. Entidades AppUserPowerup, AppUserTheme, PlanPayment e UserRole não possuem esse método por falta de necessidade.

## Para mais informações
Visite a documentação completa no endpoint /swagger-ui/index.html!
