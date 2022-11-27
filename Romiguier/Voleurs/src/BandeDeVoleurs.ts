import { Voleur } from "./Voleur";

export class BandeDeVoleurs {
    private _voleurs: Voleur[] = [];
    constructor(nombreDeVoleurs:number = 1) {
        for(let i=0; i<nombreDeVoleurs;i++) {
            this._voleurs.push(new Voleur());
        }
    }

    get voleurs() {
        return this._voleurs;
    }

    get voleursNonOccupes() {
        return this._voleurs.filter(v => v.coffreEnCours === undefined);
    }

    get voleursOccupes() {
        return this._voleurs.filter(v => v.coffreEnCours !== undefined);
    }
}