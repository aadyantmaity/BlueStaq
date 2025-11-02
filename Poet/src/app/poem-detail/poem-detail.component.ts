import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { PoemDataService } from '../../services/poem-data.service';
import { Poem } from '../../models/poem.model';

@Component({
  selector: 'app-poem-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './poem-detail.component.html',
  styleUrl: './poem-detail.component.css'
})
export class PoemDetailComponent implements OnInit {
  poem: Poem | undefined;
  isLoading: boolean = false;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private poemDataService: PoemDataService
  ) {}

  ngOnInit(): void {
    this.isLoading = true;
    const id = this.route.snapshot.paramMap.get('id');
    
    if (!id) {
      this.error = 'Poem ID is required';
      this.isLoading = false;
      return;
    }

    // Try to get poem from cache
    this.poem = this.poemDataService.getPoem(id);
    
    if (!this.poem) {
      // If not in cache, parse the key and show error
      // In a real app, we might fetch from API here
      this.error = 'Poem not found. Please navigate back and select a poem.';
    }
    
    this.isLoading = false;
  }

  goBack(): void {
    this.router.navigate(['/']);
  }
}

