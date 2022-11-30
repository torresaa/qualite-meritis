import { BandeDeVoleurs } from "./BandeDeVoleurs";
import { Coffre } from "./Coffre";

describe("Bande de voleurs tests", () => {
    test("doit créer une bande de 3 voleurs", () => {
        const bandeDeVoleurs = new BandeDeVoleurs(3);
        expect(bandeDeVoleurs.voleurs.length).toBe(3);
    })

    test("doit créer une bande de 1 voleur minimum", () => {
        const bandeDeVoleurs = new BandeDeVoleurs();
        expect(bandeDeVoleurs.voleurs.length).toBe(1);
    })

})