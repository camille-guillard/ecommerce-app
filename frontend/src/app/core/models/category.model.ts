export interface Category {
  id: number;
  name: string;
  displayName: string;
  parentId: number | null;
  children: Category[];
}
