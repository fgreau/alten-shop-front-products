import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Product } from "./product.model";
import { RestApiService } from "../shared/utils/rest-api/rest-api.service";
import { PatchProduct } from "./patch-product.model";
import { Observable } from "rxjs";

const PRODUCTS_API_URL = 'http://localhost:8080/products'

@Injectable({
  providedIn: 'root'
})
export class ProductService extends RestApiService<Product> {

  constructor(protected http: HttpClient) {
    super(http, PRODUCTS_API_URL)
  }

  update(id: number, patchValues: PatchProduct): Observable<Product> {
    let url = `${ this.apiUrl }/${id}`
    return this.http.patch<Product>(url, patchValues)
  }

  create(patchProduct: PatchProduct): Observable<Product> {
    return this.http.post<Product>(this.apiUrl, patchProduct)
  }

  productToPatchProduct(product: Product): PatchProduct {
    return {
      code: product.code,
      name: product.name,
      description: product.description.trim() !== '' ? product.description : undefined,
      price: product.price,
      quantity: product.quantity,
      category: product.category,
      image: product.image,
      rating: product.rating,
    }
  }

}
