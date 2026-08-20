
export interface UserModelTypes {
  fullname: string;
  avatar: string;
  email: string;
  token: string;
  name: string;
  phone: string;
 [key: string]: any | null;
}


// types/AuthData.ts

export interface User {
  id: number;
  fullName: string;
  email: string;
  username: string;
  role: string|null;
  status: string|null;
}

export interface UserAuthData {
  type: string;
  user: User;
  token: string;
  [key: string]: any | null;
}
