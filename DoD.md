# ODA 2 Valmiin määritelmä - Definition of Done 

Tämä dokumentti kuvaa ODA2 Ratkaisun ja Palvelun toteuttamisen aikaiset hyväksymis- ja muutoshallintakäytännöt eli Definition of Done. 

## Tehtävän valmistuminen

Vaatimusmäärittelydokumenteissa on kuvattu tehtävän tavoitetila. Uudella toiminnallisuudella on käyttäjätarina tai käyttötapaus, muutoksella päivitetty käyttäjätarina tai käyttötapaus. Tehtävä on valmis kun tavoitetila on saavutettu. Tehtävä ei voi olla osittain valmis. Tämä siksi että uudessa iteraatiossa on aloitettava siitä tilanteesta missä ollaan, puhtaalta pöydältä ja unohtaa uponneet kustannukset. Kehitystiimi yhdessä muotoilee ja ylläpitää DoD-dokumenttia projektin ajan. Tehtävä on valmis kun: 

**1. Tehtävän tuotos on läpäissyt yksikkötestin.** Tehtävän valmistuessa sen tulee läpäistä yksikkötestit, yksikkötestien tulee olla dokumentoitu ja yksikkötestien tulee olla toistettavissa käsin testiympäristössä. 

**2. Koodi on katselmoitu ja talletettu.** Koodin tulee olla kollegan katselmoima, kommentoitu ja tallennettu versionhallintaohjeen mukaan. Koodi tulee voida kääntää ja asentaa ilman virheitä. 

**3. Tehtävä on testattavissa.** Valmiista tehtävistä kirjataan järjestelmätesti tai käyttötestaustehtävä, riippuen onko kyseessä tekninen ominaisuus vai käyttökokemusmuutos. Tuoteomistajan tai testaajan on voitava toistaa testi toteutusta hyväksyttäessä. 

**4. Dokumentit on päivitetty.** Toteutuksen perusteella dokumentointi tarkennetaan uusien versioiden ehdotuksina toiminnalliseen määrittelyyn, rajapintojen käyttöohjeeseen, käyttäjäohjeisiin, järjestelmän asennusohjeisiin tai suunnitteluohjeisiin. Tieto 3. osapuolen lisensseistä on päivitetty COPYING-dokumenttiin.

**5. Tehtävänhallinta on ajan tasalla.** Kuittaukset tehtävien valmistumisesta kuitataan projektinhallintajärjestelmään reaaliajassa. Tehtävänhallinta on hankkeen teknisten reunaehtojen sekä valittujen käytäntöjen mukaan tehty kuten Lean ja Scrum. 

**6. Jäännöstehtävät on kirjattu.** Keskeneräiseksi jääneet tehtävät jaetaan valmistuneeseen ja valmistumattomaan osioon, jäännöstehtävään. Jäännöstehtävälle annetaan aina suuntaa-antava työmääräarvio sitä kuvattaessa. 

Toteutuksen aikana tai määrittelydokumentissa tunnistetuista vaihtoehtoisista käyttötapauksista kirjataan toiminnalliseen määrittelyyn niiden määrittelyjen puuttuessakin aina maininta: "ei ole" tai "määrittämättä". 

**7. Valmis tuotos on demottu tiimille.** Tuoteomistaja ja tiimi vahvistaa demottaessa tehtävän valmistumisen. 

**8. Tuoteomistaja on kuitannut tuotoksen.** Tuoteomistaja kuittaa tehtävän kirjallisesti valmiiksi.  

## Valmiin tasot

**MVP.** Tuotos toimii teknisesti testipenkissä ja on pilotoitavissa. 

**1.0.** Tuotos on integroitu ja on testattavissa päästä päähän reaaliaikaisilla datoilla alpha-jakeluna. 

**1.N.** Tuotos on jaeltavissa kaikille käyttäjäryhmille koko organisaatiossa beta-jakeluna. 

**2.0.** Tuotos on jaeltu laajasti ja ansaitsee kiitosta käyttäjiltä. Kehittäjät voivat olla vilpittömästi ylpeitä tuotoksesta, ja vain harjaantuneimmat käyttäjät saattavat huomata parannustarpeita. Tuotosta voi suositella muiden organisaatioiden käyttöön. 

## MVP ja työnäyte

Työnäytettä arvioitaessa MVP eri osat on painotettu tehtävän teknisen kompleksisuuden ja odotetun haastavuuden mukaan kokoluokkiin XS-S-M-L-XL. Tehtävän koko huomioidaan pisteytyksessä. Tehtävän ollessa osittain valmis, jäännöstehtävien kokoluokkaa vastaavat pisteet vähennetään tehtävän pisteistä. 

## Muutoshallinta 

Muutoksien ehdottaminen kuuluu kaikille projektiin työtä tekeville. Muutoksen hyväksyy tehtävän tai dokumentin omistaja. 

Iteraation aikana ilmenneet jäännöstehtävät, ehdotetut sivukäyttötapaukset muotoiluohjemuutokset ja muut muutosehdotukset nostetaan kehitysjonoon työnjohton ja tuoteomistajien arvioitavaksi ennen iteraation valmistumiskatselmointia.  

## Tehtävät toteutuksen aikana

Tehtävät tulee jakaa enintään 1 htp alitehtäviin. Mielummin niin että tehtävän saa suljettua aloituspäivänä. Tehtävien ajan tasalle kirjaaminen tulee tehdä aina viimeistään päivän päätteeksi. Jos päivittäispalaveria ei ole seuraavana työpäivänä, mukaan lisätään kommentti etenemismenestyksestä ja mahdollisista poikkeamista. 
