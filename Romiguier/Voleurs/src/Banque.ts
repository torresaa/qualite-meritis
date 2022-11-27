import type { BandeDeVoleurs } from "./BandeDeVoleurs";
import type { Coffre } from "./Coffre";

export class Banque {
    private _coffres: Coffre[] = [];
    ajouteCoffre(coffre:Coffre) {
        if(this._coffres.find(c => c.id === coffre.id)) {
            throw new Error("Ce coffre existe déjà dans la banque.");
        }
        this._coffres.push(coffre);
    }
    get tousLesCoffres() {
        return this._coffres;
    }
    get lesCoffresOuverts() {
        return this._coffres.filter(c => c.estOuvert);
    }

    get lesCoffresFermes() {
        return this._coffres.filter(c => c.estFerme);
    }

    get lesCoffresEnCoursDOuverture() {
        return this._coffres.filter(c => c.estEnCoursDOuverture);
    }
}