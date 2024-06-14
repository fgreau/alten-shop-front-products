import { PageableParameters } from "./pageable-parameters.model";

export interface PageableResponse<T> {
  _embedded: DtoList<T>
  _links: Links
  page: PageableParameters
}

export interface DtoList<T> {
  [key: string]: Array<T>
}

export interface Links {
  first: Link
  self: Link
  next: Link
  last: Link
}

export interface Link {
  href: string
}