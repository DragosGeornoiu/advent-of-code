package ro.dragos.geornoiu.year2023.day07.first.part;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Solution {
  public static void main(String[] args) {
    List<Hand> hands = readInputFirstPart();
    int totalFirstPart = 0;
    for (int i = 0; i < hands.size(); i++) {
      totalFirstPart += hands.get(i).getBid() * (i + 1);
      System.out.println(hands.get(i).getCards());
    }
    System.out.println("totalFirstPart: " + totalFirstPart);
  }

  private static List<Hand> readInputFirstPart() {
    try (
      BufferedReader br =
          new BufferedReader(
              new FileReader("src/main/java/ro/dragos/geornoiu/year2023/day07/input.txt"))) {

      return br.lines().toList().stream()
          .map(
              line -> {
                String[] parts = line.split(" ");
                return parseHand(parts[0].trim(), Integer.parseInt(parts[1].trim()));
              })
          .sorted(Hand::compareHands)
          .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }

    return Collections.emptyList();
  }

  private static Hand parseHand(String handString, long bid) {
    List<Card> cards = new ArrayList<>();
    for (char c : handString.toCharArray()) {
      Rank rank = getRankFromChar(c);
      cards.add(new Card(rank));
    }
    return new Hand(cards, bid);
  }

  private static Rank getRankFromChar(char c) {
    return switch (c) {
      case '2' -> Rank.TWO;
      case '3' -> Rank.THREE;
      case '4' -> Rank.FOUR;
      case '5' -> Rank.FIVE;
      case '6' -> Rank.SIX;
      case '7' -> Rank.SEVEN;
      case '8' -> Rank.EIGHT;
      case '9' -> Rank.NINE;
      case 'T' -> Rank.TEN;
      case 'J' -> Rank.JACK;
      case 'Q' -> Rank.QUEEN;
      case 'K' -> Rank.KING;
      case 'A' -> Rank.ACE;
      default -> throw new IllegalArgumentException("Invalid card rank: " + c);
    };
  }
}

enum Rank {
  TWO(2),
  THREE(3),
  FOUR(4),
  FIVE(5),
  SIX(6),
  SEVEN(7),
  EIGHT(8),
  NINE(9),
  TEN(10),
  JACK(11),
  QUEEN(12),
  KING(13),
  ACE(14);

  private final int value;

  Rank(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}

record Card(Rank rank) {

  public Rank getRank() {
    return rank;
  }

}

enum HandRank {
  FIVE_OF_A_KIND,
  FOUR_OF_A_KIND,
  FULL_HOUSE,
  THREE_OF_A_KIND,
  TWO_PAIR,
  ONE_PAIR,
  HIGH_CARD
}

record Hand(List<Card> cards, long bid) {

  public long getBid() {
    return bid;
  }

  public List<Card> getCards() {
    return cards;
  }

  public HandRank getHandRank() {
    Map<Rank, Long> rankCount =
        cards.stream().collect(Collectors.groupingBy(Card::getRank, Collectors.counting()));

    if (rankCount.containsValue(5L)) {
      return HandRank.FIVE_OF_A_KIND;
    } else if (rankCount.containsValue(4L)) {
      return HandRank.FOUR_OF_A_KIND;
    } else if (rankCount.containsValue(3L) && rankCount.containsValue(2L)) {
      return HandRank.FULL_HOUSE;
    } else if (rankCount.containsValue(3L)) {
      return HandRank.THREE_OF_A_KIND;
    } else if (rankCount.values().stream().filter(count -> count == 2L).count() == 2) {
      return HandRank.TWO_PAIR;
    } else if (rankCount.containsValue(2L)) {
      return HandRank.ONE_PAIR;
    } else {
      return HandRank.HIGH_CARD;
    }
  }

  public static int compareHands(Hand hand1, Hand hand2) {
    int rankComparison = Integer.compare(hand1.getHandRank().ordinal(),
        hand2.getHandRank().ordinal());

    if (rankComparison != 0) {
      // If hand ranks are different, return the comparison result
      return -rankComparison;
    } else {
      // If hand ranks are the same, compare individual card ranks
      List<Card> cards1 = hand1.getCards();
      List<Card> cards2 = hand2.getCards();

      for (int i = 0; i < cards1.size(); i++) {
        int cardComparison = Integer.compare(cards1.get(i).getRank().getValue(),
            cards2.get(i).getRank().getValue());
        if (cardComparison != 0) {
          // If card ranks are different, return the comparison result
          return cardComparison;
        }
      }

      // If all cards are equal, return 0 (hands are considered equal)
      return 0;
    }
  }

  @Override
  public String toString() {
    return "Hand{" + "bid=" + bid + ", cards=" + cards + '}';
  }
}
