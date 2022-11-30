import type { Coffre } from "./Coffre";

export class Voleur {
  private _tempsTotalDeHack: number = 0;

  ouvreCoffre(coffre: Coffre) {
    this._tempsTotalDeHack += coffre.ouvre();
  }

  get tempsTotalDeHack(): number {
    return this._tempsTotalDeHack;
  }
}
