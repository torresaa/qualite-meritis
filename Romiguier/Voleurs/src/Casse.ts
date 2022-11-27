import type { BandeDeVoleurs } from "./BandeDeVoleurs";
import type { Banque } from "./Banque";

export class Casse {
  private _tempsDuCasse = 0;
  constructor(private banque: Banque, private bandeDeVoleurs: BandeDeVoleurs) {}
  get tempsDuCasse() {
    return this._tempsDuCasse;
  }
  demarrer() {
    do {
      this._tempsDuCasse++;
      this.assignerLesCoffresAuxVoleursNonOccupes();
      this.testerLesCombinaisons();
    } while (this.banque.lesCoffresEnCoursDOuverture.length > 0);
  }
  assignerLesCoffresAuxVoleursNonOccupes() {
    if (this.banque.lesCoffresFermes.length > 0) {
      for (const voleurNonOccupe of this.bandeDeVoleurs.voleursNonOccupes) {
        voleurNonOccupe.assignerCoffre(this.banque.lesCoffresFermes[0]);
      }
    }
  }
  testerLesCombinaisons() {
    for (const voleurOccupe of this.bandeDeVoleurs.voleursOccupes) {
      voleurOccupe.testeCombinaison();
    }
  }
}
