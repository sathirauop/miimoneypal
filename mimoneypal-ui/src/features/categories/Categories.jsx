import { useState } from 'react';
import { useCategoriesList } from './useCategoriesQuery';

/**
 * Categories management page.
 * Displays income and expense categories with ability to filter and manage them.
 *
 * TODO: Add full CRUD functionality with forms and modals
 * TODO: Add category color/icon selection
 * TODO: Add filter by type (INCOME/EXPENSE)
 * TODO: Add delete confirmation dialog
 */
export default function Categories() {
  const [includeArchived, setIncludeArchived] = useState(false);

  const { data, isLoading, error } = useCategoriesList({ includeArchived });

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-full">
        <p className="text-muted-foreground">Loading categories...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex items-center justify-center h-full">
        <p className="text-destructive">Error loading categories: {error.message}</p>
      </div>
    );
  }

  const categories = data?.categories || [];

  return (
    <div className="container mx-auto p-4 pb-20">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Categories</h1>
        <button
          onClick={() => setIncludeArchived(!includeArchived)}
          className="text-sm text-muted-foreground underline"
        >
          {includeArchived ? 'Hide' : 'Show'} Archived
        </button>
      </div>

      {categories.length === 0 ? (
        <div className="text-center py-12">
          <p className="text-muted-foreground">No categories found.</p>
          <p className="text-sm text-muted-foreground mt-2">
            Create your first category to get started.
          </p>
        </div>
      ) : (
        <div className="grid gap-4">
          {categories.map((category) => (
            <div
              key={category.id}
              className="border rounded-lg p-4 hover:bg-accent transition-colors"
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  {category.color && (
                    <div
                      className="w-4 h-4 rounded-full"
                      style={{ backgroundColor: category.color }}
                    />
                  )}
                  <div>
                    <h3 className="font-medium">{category.name}</h3>
                    <p className="text-sm text-muted-foreground">
                      {category.type}
                      {category.is_archived && ' • Archived'}
                      {category.is_system && ' • System'}
                    </p>
                  </div>
                </div>
                {!category.is_system && (
                  <div className="flex gap-2">
                    <button className="text-sm text-primary hover:underline">
                      Edit
                    </button>
                    <button className="text-sm text-destructive hover:underline">
                      Delete
                    </button>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      <div className="mt-6 p-4 bg-muted rounded-lg">
        <p className="text-sm text-muted-foreground">
          <strong>Total Categories:</strong> {data?.total || 0}
        </p>
      </div>
    </div>
  );
}
