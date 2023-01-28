import {Role} from "./role";

export class User {
  id!: number;
  username!: string;
  password!: string;
  email!: string;
  cityAddress!: string;
  phoneNumber!: string;
  roles!: Role[];
  creditCardNumber!: string;
}
