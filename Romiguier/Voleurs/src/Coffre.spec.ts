import { Coffre } from "./Coffre";

describe("Coffre tests", () => {
      test("un coffre doit être créé avec un nombre de combinaisons possibles", () => {
        const nombreDePositions = 5;
        const nombreDeSymboles = 4;
        const coffre = new Coffre({id: 1,nombreDePositions, nombreDeSymboles});
        expect(coffre.nombreDeCombinaisonsRestantes).toBe(Math.pow(4,5));
    })

    test("un coffre est initialement fermé", () => {
        const nombreDePositions = 5;
        const nombreDeSymboles = 4;
        const coffre = new Coffre({id: 1, nombreDePositions, nombreDeSymboles});
        expect(coffre.estFerme).toBeTruthy();
    })

    test("un coffre peut être en cours d'ouverture", () => {
        const nombreDePositions = 5;
        const nombreDeSymboles = 4;
        const coffre = new Coffre({id: 1,nombreDePositions, nombreDeSymboles});
        coffre.reserver();
        expect(coffre.estEnCoursDOuverture).toBeTruthy();
    })

    test("un coffre peut être ouvert", () => {
        const nombreDePositions = 1;
        const nombreDeSymboles = 2;
        const coffre = new Coffre({id:1, nombreDePositions, nombreDeSymboles});
        coffre.testeCombinaison();
        coffre.testeCombinaison();
        expect(coffre.estOuvert).toBeTruthy();
    })

    test("si on teste une combinaison sur un coffre alors son nombre restant de combinaisons doit être diminué de 1", () => {
        const nombreDePositions = 1;
        const nombreDeSymboles = 2;
        const coffre = new Coffre({id:1, nombreDePositions, nombreDeSymboles});
        const nombreInitialDeCombinaisonsRestantes = coffre.nombreDeCombinaisonsRestantes;
        coffre.testeCombinaison();
        expect(coffre.nombreDeCombinaisonsRestantes).toBe(nombreInitialDeCombinaisonsRestantes-1);
    })
})