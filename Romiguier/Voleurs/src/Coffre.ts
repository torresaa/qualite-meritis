import type { EtatCoffre } from "./EtatCoffre";

type CoffreParams = {
  id: number;
  nombreDePositions: number;
  nombreDeSymboles: number;
};

export class Coffre {
  private _id: number;
  private _nombreDeCombinaisonsRestantes: number = 0;
  private _etat: EtatCoffre = "fermé";
  constructor({ id, nombreDePositions, nombreDeSymboles }: CoffreParams) {
    this._nombreDeCombinaisonsRestantes = Math.pow(
      nombreDeSymboles,
      nombreDePositions
    );
    this._id = id;
  }

  get id() {
    return this._id;
  }

  get nombreDeCombinaisonsRestantes(): number {
    return this._nombreDeCombinaisonsRestantes;
  }

  get estOuvert(): boolean {
    return this._etat === "ouvert";
  }

  get estFerme(): boolean {
    return this._etat === "fermé";
  }

  get estEnCoursDOuverture(): boolean {
    return this._etat === "en cours d'ouverture";
  }

  reserver() {
    this._etat = "en cours d'ouverture";
  }

  testeCombinaison() {
    if (this._etat === "fermé") {
      this._etat = "en cours d'ouverture";
    }
    if (this._etat !== "ouvert") {
      this._nombreDeCombinaisonsRestantes--;
      if (this._nombreDeCombinaisonsRestantes === 0) {
        this._etat = "ouvert";
      }
    }
  }
}
