import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { PoetryService } from '../../services/poetry.service';
import { PoemDataService } from '../../services/poem-data.service';
import { Poem } from '../../models/poem.model';

@Component({
  selector: 'app-poem-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './poem-list.component.html',
  styleUrl: './poem-list.component.css'
})
export class PoemListComponent implements OnInit {
  searchQuery: string = '';
  searchType: 'author' | 'title' | 'both' = 'both';
  poems: Poem[] = [];
  isLoading: boolean = false;
  error: string | null = null;
  hasSearched: boolean = false;

  // Preview settings
  readonly previewLines = 5; // Show first 5 lines in preview

  constructor(
    private poetryService: PoetryService,
    private poemDataService: PoemDataService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Load a random poem on startup
    this.loadRandomPoem();
  }

  onSearch(): void {
    if (!this.searchQuery.trim()) {
      this.error = 'Please enter a search term';
      return;
    }

    this.hasSearched = true;
    this.isLoading = true;
    this.error = null;
    this.poems = [];

    const searchTerm = this.searchQuery.trim();

    let searchObservable;
    
    if (this.searchType === 'author') {
      searchObservable = this.poetryService.searchByAuthor(searchTerm);
    } else if (this.searchType === 'title') {
      searchObservable = this.poetryService.searchByTitle(searchTerm);
    } else {
      // For 'both', we'll search separately and combine results
      this.poetryService.searchByAuthor(searchTerm).subscribe({
        next: (authorResults) => {
          this.poetryService.searchByTitle(searchTerm).subscribe({
            next: (titleResults) => {
              const combined = [...authorResults, ...titleResults];
              const unique = this.removeDuplicatePoems(combined);
              this.handleSearchResults(unique);
            },
            error: (err) => {
              this.handleSearchResults(authorResults);
              if (err) {
                console.error('Title search error:', err);
              }
            }
          });
        },
        error: (authorErr) => {
          this.poetryService.searchByTitle(searchTerm).subscribe({
            next: (titleResults) => {
              this.handleSearchResults(titleResults);
            },
            error: (err) => {
              this.handleError(err);
            }
          });
        }
      });
      return;
    }

    searchObservable.subscribe({
      next: (results) => this.handleSearchResults(results),
      error: (err) => this.handleError(err)
    });
  }

  loadRandomPoem(): void {
    this.isLoading = true;
    this.error = null;
    this.hasSearched = false;
    
    this.poetryService.getRandomPoem().subscribe({
      next: (results) => {
        this.poems = results;
        this.isLoading = false;
        if (results.length === 0) {
          this.error = 'No poems found. Try a different search.';
        }
      },
      error: (err) => this.handleError(err)
    });
  }

  private handleSearchResults(results: Poem[]): void {
    this.isLoading = false;
    if (results.length === 0) {
      this.error = 'No poems found. Try a different search term.';
      this.poems = [];
    } else {
      this.poems = results;
      this.error = null;
    }
  }

  private handleError(error: any): void {
    this.isLoading = false;
    this.error = error?.message || 'Failed to fetch poems. Please try again.';
    this.poems = [];
    console.error('Search error:', error);
  }

  private removeDuplicatePoems(poems: Poem[]): Poem[] {
    const seen = new Set<string>();
    return poems.filter(poem => {
      const key = `${poem.title}-${poem.author}`;
      if (seen.has(key)) {
        return false;
      }
      seen.add(key);
      return true;
    });
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.poems = [];
    this.error = null;
    this.hasSearched = false;
    this.loadRandomPoem();
  }

  /**
   * Get preview lines for a poem
   */
  getPreviewLines(poem: Poem): string[] {
    return poem.lines.slice(0, this.previewLines);
  }

  /**
   * Check if poem has more lines than preview
   */
  hasMoreLines(poem: Poem): boolean {
    return poem.lines.length > this.previewLines;
  }

  /**
   * Navigate to poem detail page
   */
  viewFullPoem(poem: Poem, event?: Event): void {
    if (event) {
      event.stopPropagation();
    }
    // Store poem in service and navigate
    const key = this.poemDataService.setPoem(poem);
    this.router.navigate(['/poem', key]);
  }

  /**
   * Get poem route key
   */
  getPoemRouteKey(poem: Poem): string {
    return this.poemDataService.generateKey(poem);
  }

  /**
   * Handle card click
   */
  onCardClick(poem: Poem): void {
    this.viewFullPoem(poem);
  }

  /**
   * Get placeholder text based on selected search type
   */
  getPlaceholderText(): string {
    switch (this.searchType) {
      case 'author':
        return 'Enter author name...';
      case 'title':
        return 'Enter poem title...';
      case 'both':
      default:
        return 'Enter author name or poem title...';
    }
  }
}

