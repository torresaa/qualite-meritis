import { Coffre } from "./Coffre";

describe("Coffre tests", () => {
      test("un coffre doit être créé avec un nombre de combinaisons possibles", () => {
        const nombreDePositions = 5;
        const nombreDeSymboles = 4;
        const coffre = new Coffre({nombreDePositions, nombreDeSymboles});
        expect(coffre.nombreDeCombinaisonsRestantes).toBe(Math.pow(4,5));
    })

    test("un coffre est initialement fermé", () => {
        const nombreDePositions = 1;
        const nombreDeSymboles = 2;
        const coffre = new Coffre({ nombreDePositions, nombreDeSymboles});
        expect(coffre.estFerme).toBeTruthy();
    })

    test("un coffre est ouvert après l'avoir hacké", () => {
        const nombreDePositions = 1;
        const nombreDeSymboles = 2;
        const coffre = new Coffre({ nombreDePositions, nombreDeSymboles});
        coffre.ouvre();
        expect(coffre.estFerme).toBeFalsy();
    })

    test("si un coffre est hacké alors on obtient le temps passé à l'avoir hacké", () => {
        const nombreDePositions = 1;
        const nombreDeSymboles = 2;
        const coffre = new Coffre({ nombreDePositions, nombreDeSymboles});
        const nombreTotalDeCombinaisons = coffre.nombreDeCombinaisonsRestantes;
        const tempsPasse =  coffre.ouvre();
        expect(tempsPasse).toBe(nombreTotalDeCombinaisons);
    })
})