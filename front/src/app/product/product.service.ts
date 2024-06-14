import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Product } from "./product.model";
import { RestApiService } from "../shared/utils/rest-api/rest-api.service";

const PRODUCTS_API_URL = 'http://localhost:8080/products'

@Injectable({
  providedIn: 'root'
})
export class ProductService extends RestApiService<Product> {

  constructor(protected http: HttpClient) {
    super(http, PRODUCTS_API_URL)
  }
}
