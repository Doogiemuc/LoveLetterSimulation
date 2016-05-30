# Love Letter Simulation

This is a little simulation of the card game "Love Letter". It simulates different kinds of players
with different strategies.

Main class is `LoveLetterGame`

# Debug Output of one game

When you turn on the trace flag then the rounds of each game are shown. In this game the player "Best0" won cause he guessed the carded of his opponent correctly.

    INFO  running ...
    TRACE 0:   16247135438215 ( 1) ( 1) Best0(1GUARD).. draws 1GUARD plays it guesses 7 at Rand1[1GUARD]
    TRACE 1:   6247135438215 ( 1)1 ( 1) Rand1[1GUARD] draws 6KING plays it without effect.
    TRACE 2:   247135438215 ( 1)1 ( 1)6 Best0(1GUARD).. draws 2PRIEST plays it and sees Rand1[1GUARD]
    TRACE 3:   47135438215 ( 1)12 ( 1)6 Rand1[1GUARD] draws 4MAID plays it and is save.
    TRACE 4:   7135438215 ( 1)12 ( 1)64 Best0(1GUARD).1 draws 7COUNTESS plays 1GUARD of course correctly guesses 1 at Rand1[1GUARD] => CORRECT!
    TRACE 5:   135438215 ( 7)121 (X1)64 
    INFO  0: GameStats[1116247135438215, Best0(7COUNTESS).1(121) Rand1[1GUARD](64) , rounds=5, winners=[Best0(7COUNTESS).1]]
    INFO  Winners: Best0(7COUNTESS).1:100.0% Rand1[1GUARD]:0.0%

# Output of a simulation run with 1000 games

This is the output of a simulation with two players: One that always plays a random card, and the other one is the BestPlayer I could code. 

    INFO  986: GameStats[1541436528121137, Best0(8PRINCESS)..(4141513) Rand1[2PRIEST](563211) , rounds=11, winners=[Best0(8PRINCESS)..]]
    INFO  987: GameStats[8725311112154346, Best0(8PRINCESS).5(23) Rand1[5PRINCE](7) , rounds=3, winners=[Best0(8PRINCESS).5]]
    INFO  988: GameStats[2135184475311621, Best0(3BARON)..(21) Rand1[4MAID](185) , rounds=4, winners=[Best0(3BARON)..]]
    INFO  989: GameStats[1475815412632131, Best0(7COUNTESS)..(11) Rand1[4MAID](4585) , rounds=4, winners=[Best0(7COUNTESS)..]]
    INFO  990: GameStats[2417123514185613, Best0(1GUARD).4(21) Rand1[4MAID](7) , rounds=3, winners=[Best0(1GUARD).4]]
    INFO  991: GameStats[6511574211218433, Best0(8PRINCESS).1(1762123) Rand1[1GUARD](155144) , rounds=11, winners=[Best0(8PRINCESS).1]]
    INFO  992: GameStats[8617241235541113, Best0(8PRINCESS).6(121) Rand1[6KING](74) , rounds=5, winners=[Best0(8PRINCESS).6]]
    INFO  993: GameStats[2112815633145417, Best0(8PRINCESS).1(21) Rand1[1GUARD](2) , rounds=3, winners=[Best0(8PRINCESS).1]]
    INFO  994: GameStats[2275134415118361, Best0(6KING)..(2147135) Rand1[8PRINCESS](235141) , rounds=11, winners=[Rand1[8PRINCESS]]]
    INFO  995: GameStats[1131521621378544, Best0(6KING)..(135217) Rand1[8PRINCESS](11231) , rounds=10, winners=[Rand1[8PRINCESS]]]
    INFO  996: GameStats[1811724216153345, Best0(7COUNTESS)..(11411) Rand1[3BARON](122685) , rounds=10, winners=[Best0(7COUNTESS)..]]
    INFO  997: GameStats[1761531812441325, Best0(6KING)..(1) Rand1[7COUNTESS]() , rounds=1, winners=[Best0(6KING)..]]
    INFO  998: GameStats[5735216314811412, Best0(5PRINCE)..(351) Rand1[7COUNTESS](2) , rounds=3, winners=[Best0(5PRINCE)..]]
    INFO  999: GameStats[7512614841351321, Best0(8PRINCESS)..(117152) Rand1[3BARON](2544361) , rounds=11, winners=[Best0(8PRINCESS)..]]
    INFO  Winners: Best0(8PRINCESS)..:59.599999999999994% Rand1[3BARON]:40.6%
    
Result after 1000 games: BestPlayer won 60% of all games. 