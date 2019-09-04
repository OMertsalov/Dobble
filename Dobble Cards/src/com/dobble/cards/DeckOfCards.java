package com.dobble.cards;

import java.util.ArrayList;
import java.util.Collections;


public class DeckOfCards {

    private ArrayList<Card> cards;
    private ArrayList<ImageOnCard> images;
    private int nbrOfCards;

    public DeckOfCards()
    {
        nbrOfCards=0;
        createListOfImages();
        createDeckOfCards();
        Collections.shuffle(cards);
        setCardsNumber();

    }

    private void createListOfImages() {
        images = new ArrayList<>();
        for(int i=0;i<57;i++)
            images.add(new ImageOnCard
                    ("tmp/images/ImagesOnCards/Picture (" + (i + 1) + ").png",
                            i + 1));
    }

    private void createDeckOfCards() {
        cards = new ArrayList<>();
        createNewCard(29,25,2,13,27,19,12,57);
        createNewCard(29,34,37,22,53,15,31,50);
        createNewCard(1,14,54,29,55,51,36,52);
        createNewCard(49,29,11,23,9 ,35,5 ,45);
        createNewCard(8 ,26,7 ,29,20,42,17,47);
        createNewCard(30,40,3,29,18,38,16,6);
        createNewCard(41,24,21,10,33,32,56,29);
        createNewCard(14,53,42,2,6,45,48,56);
        createNewCard(50,41,47,12,16,35,48,1);
        createNewCard(36,48,3,8,37,13,5,32);
        createNewCard(21,34,23,48,27,26,55,40);
        createNewCard(11,20,38,48,31,52,10,25);
        createNewCard(19,22,33,51,48,17,30,9);
        createNewCard(7,48,49,15,24,18,54,57);
        createNewCard(23,24,36,50,2,30,44,20);
        createNewCard(37,11,12,17,18,56,55,44);
        createNewCard(44,9,7,6,52,34,41,13);
        createNewCard(31,32,51,42,16,27,44,49);
        createNewCard(25,45,21,44,47,3,22,54);
        createNewCard(40,14,44,10,35,19,15,8);
        createNewCard(33,1,5,44,38,26,53,57);
        createNewCard(47,49,40,37,2,46,52,33);
        createNewCard(8,46,51,24,45,34,38,12);
        createNewCard(13,56,46,54,26,30,35,31);
        createNewCard(22,5,20,14,18,46,27,41);
        createNewCard(46,17,1,23,15,32,25,6);
        createNewCard(11,36,16,46,19,21,7,53);
        createNewCard(42,50,46,10,3,55,9,57);
        createNewCard(5,54,16,43,17,34,2,10);
        createNewCard(14,3,12,7,31,23,33,43);
        createNewCard(13,11,1,24,43,40,42,22);
        createNewCard(47,38,15,56,9,36,43,27);
        createNewCard(30,25,41,55,43,53,49,8);
        createNewCard(26,52,43,45,32,50,18);
        createNewCard(37,6,21,51,35,20,43,57);
        createNewCard(9,4,2,18,8,21,31,1);
        createNewCard(22,10,4,6,49,12,26,36);
        createNewCard(13,20,45,55,16,33,15,4);
        createNewCard(52,4,35,53,17,3,27,24);
        createNewCard(7,25,40,51,5,56,4,50);
        createNewCard(37,42,4,23,54,38,19,41);
        createNewCard(34,47,4,11,32,14,30,57);
        createNewCard(32,28,38,22,55,7,35,2);
        createNewCard(30,21,5,15,52,12,28,42);
        createNewCard(51,47,10,28,18,13,53,23);
        createNewCard(8,33,27,28,50,11,54,6);
        createNewCard(24,37,26,28,16,9,14,25);
        createNewCard(28,34,20,49,1,3,19,56);
        createNewCard(17,40,36,45,28,41,31,57);
        createNewCard(3,26,2,51,11,39,15,41);
        createNewCard(9,20,54,32,40,12,53,39);
        createNewCard(39,17,49,21,13,14,38,50);
        createNewCard(30,1,10,39,7,45,37,27);
        createNewCard(39,35,25,33,36,42,18,34);
        createNewCard(31,5,47,6,24,55,19,39);
        createNewCard(39,22,8,56,52,16,23,57);
        createNewCard(4,29,43,28,48,44,39,46);
    }

    private void createNewCard(int... nbr) {
        nbrOfCards++;
        ArrayList<ImageOnCard> eightImages = new ArrayList<>();
        for(int i=0;i<nbr.length;i++)
            eightImages.add(images.get(nbr[i]-1));
        cards.add(new Card(eightImages,nbrOfCards,
                "tmp/images/Cards/card_"+nbrOfCards+".png"));
    }

    public void setCardsNumber()
    {
        int i = 0;
        for(Card card : cards) {
            card.setNumber(i);
            i++;
        }
    }

    public ArrayList<Card> getCards() {
        return cards;
    }


}
