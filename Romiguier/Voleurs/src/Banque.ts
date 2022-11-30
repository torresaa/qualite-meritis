import type { Coffre } from "./Coffre";

export class Banque {
    private _coffres: Coffre[] = [];
    ajouteCoffre(coffre:Coffre) {
        this._coffres.push(coffre);
    }

    get lesCoffresFermes() {
        return this._coffres.filter(c => c.estFerme);
    }

}