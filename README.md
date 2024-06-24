# AltenShop
### *AltenShop is a simple FullStack demo project centered around the Springboot and Angular frameworks.*

## Getting started

To run this project, you will need the following tools :
- Maven 3.9.7 or later
- Docker : 
  - Engine 19.03.0 or later
  - Docker Compose 1.25.0 or later
- Java : JDK 21 or later

## Starting the application

To be able to run the application, you first need to start the database container.  
For the sake of simplicity, you can just run the full docker-compose with this command : `docker-compose up --build`.

When the database is up, you can build your back-end application using maven : `mvn clean compile -DskipTests`.  
Then you can simply run the application, ensuring the `dev` profile is active : `mvn spring-boot:run -D"spring-boot.run.profiles"=dev`.

Now that everything is running properly, you can either use the application through the front-end UI : [localhost:4200](http://localhost:4200/),
or use the embedded Swagger UI : [localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).

## What's left to do

Until this project can be considered done, it is still missing a few steps : 
- [ ] Unit tests for service methods
  - [x] POST
  - [ ] PATCH
  - [x] DELETE
- [ ] Writing the associated service methods
-  [ ] Plug the front-end to these methods
-  [ ] Complete this documentation


___

# Front-end

Créer un module angular "product" avec 2 composants (basés sur primeng): 
 - **products-admin** : qui liste les produits et qui permet de les administrer (ajouter, supprimer, modifier).
    Il doit être accessible à cette adresse : http://localhost:4200/admin/products
 - **products** : qui liste les produits en mode lecture seule, comme sur une boutique en ligne.
    Il doit être accessible à cette adresse : http://localhost:4200/products

Une liste de produit est disponible dans le dossier assets : `front/assets/products.json`.

Le service qui permettra de manipuler les produits doit se baser sur cette liste et être prêt à être connecté sur une API Rest ultérieurement

Le design cible est visible sur les captures d'écrans ci-dessous (et disponibles dans le dossier `front/doc`).

**Pour la partie Admin :**
![admin](front/doc/products-admin.png)

Nous vous conseillons d'utiliser le composant table de [PrimeNG](https://primeng.org/table/filter) avec les options filtre, edit, page, etc...

 **Pour la partie publique :**
![public](front/doc/products.png)

Nous vous conseillons d'utiliser le composant data view de [PrimeNG](https://primeng.org/dataview) avec les options sort, search, page, etc...


Le menu latéral gauche doit contenir les accès à ces 2 composants.

Un système de pagination doit être mis en place pour pouvoir afficher les produits par 10, 25 ou 50 comme ci-dessous :

![pagination](front/doc/pagination.png)

# Back-end (optionnel)

Si vous avez le temps vous pouvez développer un back-end permettant la gestion de produits définis plus bas.
Vous pouvez utiliser la technologie de votre choix parmis la liste suivante :

- nodejs/express
- Java/Spring Boot
- C#/.net Core
- Python/Flask


Le back-end doit gérer les API suivantes : 

| Resource           | POST                  | GET                            | PATCH                                    | PUT | DELETE           |
| ------------------ | --------------------- | ------------------------------ | ---------------------------------------- | --- | ---------------- |
| **/products**      | Create a new products | Retrieve all products          | X                                        | X   |     X            |
| **/products/1**    | X                     | Retrieve details for product 1 | Update details of product 1 if it exists | X   | Remove product 1 |

Un produit a les caractéristiques suivantes : 

``` typescript
class Product {
  id: number;
  code: string;
  name: string;
  description: string;
  price: number;
  quantity: number;
  inventoryStatus: string;
  category: string;
  image?: string;
  rating?: number;
}
```

Le back-end créé doit pouvoir gérer les produits dans une base de données SQL/NoSQL ou dans un fichier json.

## Bonus

Vous pouvez ajouter des tests Postman ou Swagger pour valider votre API
