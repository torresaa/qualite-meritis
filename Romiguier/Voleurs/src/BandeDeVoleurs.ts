import { Banque } from "./Banque";
import { Voleur } from "./Voleur";

export class BandeDeVoleurs {
  private _voleurs: Voleur[] = [];
  constructor(nombreDeVoleurs: number = 1) {
    for (let i = 0; i < nombreDeVoleurs; i++) {
      this._voleurs.push(new Voleur());
    }
  }

  get voleurs() {
    return this._voleurs;
  }

  casseBanque(banque: Banque): number {
    do {
      this.getVoleurDisponible().ouvreCoffre(banque.lesCoffresFermes[0]);
    } while (banque.lesCoffresFermes.length > 0);
    return Math.max(...this._voleurs.map((voleur) => voleur.tempsTotalDeHack));
  }

  private getVoleurDisponible() {
    return (
      this._voleurs.find(this.estVoleurAvecTempsDeHackMinimal) ??
      this._voleurs[0]
    );
  }

  private estVoleurAvecTempsDeHackMinimal(voleur: Voleur, _:number, voleurs:Voleur[]): boolean {
    return voleur.tempsTotalDeHack === Math.min(...voleurs.map((voleur) => voleur.tempsTotalDeHack));
  }
}
