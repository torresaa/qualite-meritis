import type { Coffre } from "./Coffre";

export class Voleur {
  private _coffreEnCours: Coffre | undefined = undefined;

  get coffreEnCours () {
    return this._coffreEnCours;
  }

  assignerCoffre(coffre: Coffre) {
    if (this._coffreEnCours) {
      throw new Error("Ce voleur a déjà un coffre assigné.");
    }
    if(coffre.estEnCoursDOuverture) {
        throw new Error("Ce coffre est déjà assigné.");
    }
    this._coffreEnCours = coffre;
    coffre.reserver();
  }

  testeCombinaison() {
    if (!this._coffreEnCours) {
      throw new Error(
        "Aucun coffre assigné sur lequel tester les combinaisons."
      );
    }
    this._coffreEnCours.testeCombinaison();
    if (this._coffreEnCours.estOuvert) {
      this._coffreEnCours = undefined;
    }
  }
}
