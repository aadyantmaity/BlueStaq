import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Poem } from '../models/poem.model';

@Injectable({
  providedIn: 'root'
})
export class PoetryService {
  private readonly apiBaseUrl = 'https://poetrydb.org';

  constructor(private http: HttpClient) {}

  /**
   * Search poems by author name
   * Throws an error if a 200 response is not received
   */
  searchByAuthor(author: string): Observable<Poem[]> {
    const url = `${this.apiBaseUrl}/author/${encodeURIComponent(author)}`;
    return this.http.get<Poem[]>(url, { observe: 'response' }).pipe(
      map(response => {
        // Angular HttpClient throws for non-2xx, but explicitly check for 200
        if (response.status !== 200) {
          throw new Error(`HTTP Error: Received status ${response.status} instead of 200`);
        }
        return Array.isArray(response.body) ? response.body : [];
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Search poems by title
   * Throws an error if a 200 response is not received
   */
  searchByTitle(title: string): Observable<Poem[]> {
    const url = `${this.apiBaseUrl}/title/${encodeURIComponent(title)}`;
    return this.http.get<Poem[]>(url, { observe: 'response' }).pipe(
      map(response => {
        // Angular HttpClient throws for non-2xx, but explicitly check for 200
        if (response.status !== 200) {
          throw new Error(`HTTP Error: Received status ${response.status} instead of 200`);
        }
        return Array.isArray(response.body) ? response.body : [];
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Search poems by both author and title
   * Throws an error if a 200 response is not received
   */
  searchByAuthorAndTitle(author: string, title: string): Observable<Poem[]> {
    const url = `${this.apiBaseUrl}/author,title/${encodeURIComponent(author)};${encodeURIComponent(title)}`;
    return this.http.get<Poem[]>(url, { observe: 'response' }).pipe(
      map(response => {
        // Angular HttpClient throws for non-2xx, but explicitly check for 200
        if (response.status !== 200) {
          throw new Error(`HTTP Error: Received status ${response.status} instead of 200`);
        }
        return Array.isArray(response.body) ? response.body : [];
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Get all poems by an author
   */
  getPoemsByAuthor(author: string): Observable<Poem[]> {
    return this.searchByAuthor(author);
  }

  /**
   * Get random poems
   * @param count Number of random poems to fetch (default: 6)
   * Throws an error if a 200 response is not received
   */
  getRandomPoem(count: number = 6): Observable<Poem[]> {
    const url = `${this.apiBaseUrl}/random/${count}`;
    return this.http.get<Poem[]>(url, { observe: 'response' }).pipe(
      map(response => {
        // Angular HttpClient throws for non-2xx, but explicitly check for 200
        if (response.status !== 200) {
          throw new Error(`HTTP Error: Received status ${response.status} instead of 200`);
        }
        return Array.isArray(response.body) ? response.body : [];
      }),
      catchError(this.handleError)
    );
  }

  /**
   * Get all authors
   */
  getAllAuthors(): Observable<string[]> {
    const url = `${this.apiBaseUrl}/author`;
    return this.http.get<{authors: string[]}>(url).pipe(
      catchError(this.handleError),
      map(response => response?.authors || [])
    );
  }

  /**
   * Handle HTTP errors
   * Throws an error if a 200 response is not received
   */
  private handleError = (error: HttpErrorResponse): Observable<never> => {
    let errorMessage = 'An unknown error occurred';
    
    if (error.status === 0) {
      errorMessage = 'Network error: Please check your internet connection';
    } else if (error.status !== 200) {
      // Explicitly throw error for non-200 responses
      errorMessage = `HTTP Error: ${error.status} - ${error.message || error.statusText || 'Request failed'}`;
    } else if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message || 'Request failed'}`;
    }

    console.error('Poetry API Error:', errorMessage, error);
    return throwError(() => new Error(errorMessage));
  };
}

