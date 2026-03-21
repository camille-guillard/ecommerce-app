export interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  street: string;
  city: string;
  postalCode: string;
  roles: string[];
}

export interface AuthResponse {
  token: string;
  user: User;
}
