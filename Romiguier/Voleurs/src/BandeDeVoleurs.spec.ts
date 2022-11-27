import { BandeDeVoleurs } from "./BandeDeVoleurs";
import { Coffre } from "./Coffre";

describe("Bande de voleurs tests", () => {
    test("doit créer une bande de 3 voleurs", () => {
        const bandeDeVoleurs = new BandeDeVoleurs(3);
        expect(bandeDeVoleurs.voleurs.length).toBe(3);
    })

    test("doit renvoyer une erreur si on essaie d'assigner un coffre déjà assigné", () => {
        const bandeDeVoleurs = new BandeDeVoleurs(2);
        const coffre = new Coffre({id:1, nombreDePositions: 4, nombreDeSymboles:5});
        bandeDeVoleurs.voleurs[0].assignerCoffre(coffre);
        expect(() => bandeDeVoleurs.voleurs[1].assignerCoffre(coffre)).toThrowError();
    })

    test("doit renvoyer un voleur non occupé", () => {
        const bandeDeVoleurs = new BandeDeVoleurs(3);
        const coffre = new Coffre({id:1, nombreDePositions: 4, nombreDeSymboles:5});
        const coffre2 = new Coffre({id:2, nombreDePositions: 2, nombreDeSymboles:1});
        bandeDeVoleurs.voleurs[0].assignerCoffre(coffre);
        bandeDeVoleurs.voleurs[1].assignerCoffre(coffre2);
        expect(bandeDeVoleurs.voleursNonOccupes.length).toBe(1);
    })

    test("doit renvoyer 2 voleurs occupés", () => {
        const bandeDeVoleurs = new BandeDeVoleurs(3);
        const coffre = new Coffre({id:1, nombreDePositions: 4, nombreDeSymboles:5});
        const coffre2 = new Coffre({id:2, nombreDePositions: 2, nombreDeSymboles:1});
        bandeDeVoleurs.voleurs[0].assignerCoffre(coffre);
        bandeDeVoleurs.voleurs[1].assignerCoffre(coffre2);
        expect(bandeDeVoleurs.voleursOccupes.length).toBe(2);
    })
})