type CoffreParams = {
  nombreDePositions: number;
  nombreDeSymboles: number;
};

export class Coffre {
  private _nombreDeCombinaisonsRestantes: number = 0;
  constructor({ nombreDePositions, nombreDeSymboles }: CoffreParams) {
    this._nombreDeCombinaisonsRestantes = Math.pow(
      nombreDeSymboles,
      nombreDePositions
    );
  }

  get nombreDeCombinaisonsRestantes(): number {
    return this._nombreDeCombinaisonsRestantes;
  }

  get estFerme(): boolean {
    return this._nombreDeCombinaisonsRestantes > 0;
  }

  ouvre(): number {
   const tempsPasse = this._nombreDeCombinaisonsRestantes;
   this._nombreDeCombinaisonsRestantes = 0;
   return tempsPasse;
  }
}
