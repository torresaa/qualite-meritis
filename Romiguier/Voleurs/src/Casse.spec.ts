import { BandeDeVoleurs } from "./BandeDeVoleurs";
import { Banque } from "./Banque";
import { Casse } from "./Casse";
import { Coffre } from "./Coffre";

describe("Casse tests", () => {
    test("1 voleur - 1 coffre (C = 1, D = 5), ouverture = 5s", () => {
        const bandeDeVoleurs = new BandeDeVoleurs(1);
        const banque = new Banque();
        const coffre = new Coffre({id: 1, nombreDePositions:1, nombreDeSymboles:5});
        banque.ajouteCoffre(coffre);
        const casse = new Casse(banque, bandeDeVoleurs);
        casse.demarrer();
        expect(casse.tempsDuCasse).toBe(5);
    })

    test("1 voleur - 1 coffre (C = 2, D = 5), ouverture = 25s", () => {
        const bandeDeVoleurs = new BandeDeVoleurs(1);
        const banque = new Banque();
        const coffre = new Coffre({id: 1, nombreDePositions:2, nombreDeSymboles:5});
        banque.ajouteCoffre(coffre);
        const casse = new Casse(banque, bandeDeVoleurs);
        casse.demarrer();
        expect(casse.tempsDuCasse).toBe(25);
    })

    test("2 voleurs - 2 coffres (C = 2, D = 5) (C = 1, D = 5), ouverture = 25s", () => {
        const bandeDeVoleurs = new BandeDeVoleurs(2);
        const banque = new Banque();
        const coffre = new Coffre({id: 1, nombreDePositions:2, nombreDeSymboles:5});
        const coffre2 = new Coffre({id: 2, nombreDePositions:1, nombreDeSymboles:5});
        banque.ajouteCoffre(coffre);
        banque.ajouteCoffre(coffre2);
        const casse = new Casse(banque, bandeDeVoleurs);
        casse.demarrer();
        expect(casse.tempsDuCasse).toBe(25);
    })

    test("2 voleurs - 3 coffres (C = 2, D = 5) (C = 1, D = 5) (C = 2, D = 5), ouverture = 30s", () => {
        const bandeDeVoleurs = new BandeDeVoleurs(2);
        const coffre = new Coffre({id: 1, nombreDePositions:2, nombreDeSymboles:5});
        const coffre2 = new Coffre({id: 2, nombreDePositions:1, nombreDeSymboles:5});
        const coffre3 = new Coffre({id: 3, nombreDePositions:2, nombreDeSymboles:5});
        const banque = new Banque();
        banque.ajouteCoffre(coffre);
        banque.ajouteCoffre(coffre2);
        banque.ajouteCoffre(coffre3);
        const casse = new Casse(banque, bandeDeVoleurs);
        casse.demarrer();
        expect(casse.tempsDuCasse).toBe(30);
    })

    test("2 voleurs - 5 coffres (C = 2, D = 5) (C = 1, D = 5) (C = 3, D = 5) (C = 1, D = 5) (C = 1, D = 5), ouverture = 130s", () => {
        const bandeDeVoleurs = new BandeDeVoleurs(2);
        const coffre = new Coffre({id: 1, nombreDePositions:2, nombreDeSymboles:5});
        const coffre2 = new Coffre({id: 2, nombreDePositions:1, nombreDeSymboles:5});
        const coffre3 = new Coffre({id: 3, nombreDePositions:3, nombreDeSymboles:5});
        const coffre4 = new Coffre({id: 4, nombreDePositions:1, nombreDeSymboles:5});
        const coffre5 = new Coffre({id: 5, nombreDePositions:1, nombreDeSymboles:5});
        const banque = new Banque();
        banque.ajouteCoffre(coffre);
        banque.ajouteCoffre(coffre2);
        banque.ajouteCoffre(coffre3);
        banque.ajouteCoffre(coffre4);
        banque.ajouteCoffre(coffre5);
        const casse = new Casse(banque, bandeDeVoleurs);
        casse.demarrer();
        expect(casse.tempsDuCasse).toBe(130);
    })
})