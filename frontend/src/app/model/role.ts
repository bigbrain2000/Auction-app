export class Role {
  id!: number;
  name!: string;

  constructor(id?: number, name?: string) {
    if(id && name) {
      this.id = id;
      this.name = name;
    }
  }
}
