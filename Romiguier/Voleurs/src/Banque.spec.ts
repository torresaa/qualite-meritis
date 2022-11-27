import { Banque } from "./Banque";
import { Coffre } from "./Coffre";
describe("Banque tests", () => {
  test("une nouvelle banque doit être créée", () => {
    const banque = new Banque();
    expect(banque).toBeDefined();
  });
  test("3 coffres doivent être ajoutés", () => {
    const n = 3;
    const banque = new Banque();
    for (let i = 0; i < n; i++) {
      const coffre = new Coffre({
        id: i + 1,
        nombreDePositions: 2,
        nombreDeSymboles: 3,
      });
      banque.ajouteCoffre(coffre);
    }
    expect(banque.tousLesCoffres.length).toBe(3);
  });

  test("Si un coffre existe déjà dans la banque on ne peut l'ajouter à nouveau", () => {
    const coffre = new Coffre({id: 1, nombreDePositions: 1, nombreDeSymboles: 1});
    const banque = new Banque();
    banque.ajouteCoffre(coffre);
    expect(() => banque.ajouteCoffre(coffre)).toThrowError();
  })

  test("5 coffres ajoutés, 3 ouverts, doit retourner 3 coffres ouverts", () => {
    const n = 5;
    const banque = new Banque();
    for (let i = 0; i < n; i++) {
      const coffre = new Coffre({
        id: i + 1,
        nombreDePositions: 1,
        nombreDeSymboles: 1,
      });
      banque.ajouteCoffre(coffre);
    }

    const coffres = banque.tousLesCoffres;
    coffres[1].testeCombinaison();
    coffres[2].testeCombinaison();
    coffres[4].testeCombinaison();
    expect(banque.lesCoffresOuverts.length).toBe(3);
  });

  test("5 coffres ajoutés, 3 en cours d'ouverture, doit retourner 2 coffres fermés", () => {
    const n = 5;
    const banque = new Banque();
    for (let i = 0; i < n; i++) {
      const coffre = new Coffre({
        id: i + 1,
        nombreDePositions: 2,
        nombreDeSymboles: 3,
      });
      banque.ajouteCoffre(coffre);
    }

    const coffres = banque.tousLesCoffres;
    coffres[1].testeCombinaison();
    coffres[2].testeCombinaison();
    coffres[4].testeCombinaison();
    expect(banque.lesCoffresFermes.length).toBe(2);
  });

  test("5 coffres ajoutés, 3 en cours d'ouverture, doit retourner 2 coffres fermés", () => {
    const n = 5;
    const banque = new Banque();
    for (let i = 0; i < n; i++) {
      const coffre = new Coffre({
        id: i + 1,
        nombreDePositions: 2,
        nombreDeSymboles: 3,
      });
      banque.ajouteCoffre(coffre);
    }

    const coffres = banque.tousLesCoffres;
    coffres[1].testeCombinaison();
    coffres[2].testeCombinaison();
    coffres[4].testeCombinaison();
    expect(banque.lesCoffresFermes.length).toBe(2);
  });

  test("5 coffres ajoutés, 3 en cours d'ouverture, doit retourner 3 coffres en cours d'ouverture", () => {
    const n = 5;
    const banque = new Banque();
    for (let i = 0; i < n; i++) {
      const coffre = new Coffre({
        id: i + 1,
        nombreDePositions: 2,
        nombreDeSymboles: 3,
      });
      banque.ajouteCoffre(coffre);
    }

    const coffres = banque.tousLesCoffres;
    coffres[1].testeCombinaison();
    coffres[2].testeCombinaison();
    coffres[4].testeCombinaison();
    expect(banque.lesCoffresEnCoursDOuverture.length).toBe(3);
  });
});
