import { Injectable } from '@angular/core';
import { Poem } from '../models/poem.model';

@Injectable({
  providedIn: 'root'
})
export class PoemDataService {
  private poemCache: Map<string, Poem> = new Map();

  /**
   * Store a poem in cache with a unique key
   */
  setPoem(poem: Poem): string {
    const key = this.generateKey(poem);
    this.poemCache.set(key, poem);
    return key;
  }

  /**
   * Get a poem from cache by key
   */
  getPoem(key: string): Poem | undefined {
    return this.poemCache.get(key);
  }

  /**
   * Generate a unique key from poem title and author
   */
  generateKey(poem: Poem): string {
    return `${encodeURIComponent(poem.title)}-${encodeURIComponent(poem.author)}`;
  }

  /**
   * Parse key back to title and author
   */
  parseKey(key: string): { title: string; author: string } {
    const parts = key.split('-');
    const title = decodeURIComponent(parts[0]);
    const author = decodeURIComponent(parts.slice(1).join('-'));
    return { title, author };
  }
}

