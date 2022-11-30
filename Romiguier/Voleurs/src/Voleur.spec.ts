import { Coffre } from "./Coffre";
import { Voleur } from "./Voleur";

describe("Voleur tests", () => {
    test("un voleur doit être créé", () => {
        const voleur = new Voleur();
        expect(voleur).toBeDefined();
    })

    test("si un voleur hack 2 coffres alors son temps total de hack est la somme des combinaisons possibles des 2 coffres", () => {
        const voleur = new Voleur();
        const coffre = new Coffre({ nombreDePositions: 4, nombreDeSymboles:5});
        const coffreTempsDeHack = coffre.nombreDeCombinaisonsRestantes;
        const coffre2 = new Coffre({ nombreDePositions: 2, nombreDeSymboles:1});
        const coffre2TempsDeHack = coffre2.nombreDeCombinaisonsRestantes;
        voleur.ouvreCoffre(coffre);
        voleur.ouvreCoffre(coffre2);
        expect(voleur.tempsTotalDeHack).toBe(coffreTempsDeHack + coffre2TempsDeHack);
    })
})