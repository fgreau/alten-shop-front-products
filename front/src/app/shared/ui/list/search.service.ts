import { Injectable } from "@angular/core";
import { LazyLoadEvent } from "primeng/api";
import { SearchParams } from "./search.model";
import { FilterMetadata } from "primeng/api/filtermetadata";

@Injectable()
export class SearchService {

  constructor() { }

  public formatSearchFilter(searchParams: SearchParams): SearchParams {
    if (searchParams.search) {
      return { ...searchParams, filters: { name: { value: searchParams.search } } }
    }
    return searchParams
  }

  public mapToSearchParams(lazyLoadEvent: LazyLoadEvent, defaultSortField?: string): SearchParams {
    return {
      first: lazyLoadEvent.first,
      rows: lazyLoadEvent.rows,
      sortField: lazyLoadEvent.sortField ?? defaultSortField,
      sortOrder: lazyLoadEvent.sortOrder === 1 ? 'asc' : 'desc',
      search: '',
      from: null,
      to: null,
      parentId: null,
      filters: this.removeNullFilters(lazyLoadEvent.filters),
    }
  }

  public removeNullFilters(filters: {[s: string]: FilterMetadata}): {[s: string]: FilterMetadata} {
    let newFilters: {[s: string]: FilterMetadata} = {}

    Object.entries(filters).forEach(([key, value]) => {
      if (!!value && value.value !== null) {
        newFilters[key] = value;
      }
    })

    if (Object.keys(newFilters).length <= 0) {
      return null
    }

    return newFilters
  }
}