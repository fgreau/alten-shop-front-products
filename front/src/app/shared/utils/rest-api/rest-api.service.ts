import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { SearchParams } from "../../ui/list/search.model";
import { Observable } from "rxjs";
import { PageableResponse } from "./pageable-response.model";

@Injectable({
  providedIn: 'root'
})
export class RestApiService<T> {
  protected readonly apiUrl: string

  constructor(protected http: HttpClient, apiUrl: string) {
    this.apiUrl = apiUrl
  }

  getAll(parameters: SearchParams): Observable<PageableResponse<T>> {
    let params = new HttpParams()
      .set('page', parameters.first / parameters.rows)
      .set('size', parameters.rows.toString())

    if (parameters.sortField) {
      params = params.set('sort', `${ parameters.sortField },${ parameters.sortOrder }`)
    }

    if (parameters.filters) {
      Object.entries(parameters.filters).forEach(([key, value]) => {
        params = params.set(key, value.value)
      })
    }

    return this.http.get<PageableResponse<T>>(this.apiUrl, { params })
  }

  get(id: number): Observable<T> {
    let url = `${this.apiUrl}/${id}`
    return this.http.get<T>(url)
  }

  delete(id: number): Observable<void> {
    let url = `${ this.apiUrl }/${id}`
    return this.http.delete<void>(url)
  }

}