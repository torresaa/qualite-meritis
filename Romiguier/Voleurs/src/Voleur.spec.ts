import { Coffre } from "./Coffre";
import { Voleur } from "./Voleur";

describe("Voleur tests", () => {
    test("un voleur doit être créé", () => {
        const voleur = new Voleur();
        expect(voleur).toBeDefined();
    })

    test("un voleur n'a pas de coffre assigné initialement", () => {
        const voleur = new Voleur();
        expect(voleur.coffreEnCours).toBeUndefined();
    })

    test("on peut assigner un coffre à un voleur qui n'a pas de coffre en cours", () => {
        const voleur = new Voleur();
        const coffre = new Coffre({id:1, nombreDePositions: 4, nombreDeSymboles:5});
        voleur.assignerCoffre(coffre);
        expect(voleur.coffreEnCours).toBeDefined();
    })

    test("si on assigne un coffre à un voleur qui a déjà un coffre assigné alors c'est une erreur", () => {
        const voleur = new Voleur();
        const coffre = new Coffre({id:1, nombreDePositions: 4, nombreDeSymboles:5});
        const coffre2 = new Coffre({id:2, nombreDePositions: 2, nombreDeSymboles:1});
        voleur.assignerCoffre(coffre);
        expect(() => voleur.assignerCoffre(coffre2)).toThrowError();
    })

    test("Si un voleur tente de tester une combinaison alors qu'il n'a pas de coffre assigné alors c'est une erreur", () => {
        const voleur = new Voleur();
        expect(() => voleur.testeCombinaison()).toThrowError();
    })

    test("si un voleur teste une combinaison alors son coffre assigné doit diminuer son nombre de combinaisons restantes de 1", () => {
        const voleur = new Voleur();
        const coffre = new Coffre({id:1, nombreDePositions: 4, nombreDeSymboles:5});
        const nombreInitialDeCombinaisonsRestantes = coffre.nombreDeCombinaisonsRestantes;
        voleur.assignerCoffre(coffre);
        voleur.testeCombinaison();
        expect(voleur.coffreEnCours?.nombreDeCombinaisonsRestantes).toBe(nombreInitialDeCombinaisonsRestantes - 1);
    })
})