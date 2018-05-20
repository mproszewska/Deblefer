package com.example.deblefer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.deblefer.Classes.Card;
import com.example.deblefer.Classes.CustomDialog;
import com.example.deblefer.Classes.Deck;
import com.example.deblefer.Classes.Game;
import com.example.deblefer.Classes.IconData;
import com.example.deblefer.Classes.StatisticViewAdapter;
import com.example.deblefer.Classes.Statistics;
import com.example.deblefer.Classes.StatisticsGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TexasModuleActivity extends AppCompatActivity {

    private Collection<Card> deck = Deck.getModifableDeckAsSet();
    private List<Card> table = new ArrayList<>();
    private List<Card> hand = new ArrayList<>();

    private Game game = new Game(2);
    private List<ImageView> cardImages = new ArrayList<>();
    private FloatingActionButton addButton;

    private RecyclerView recyclerView;
    private StatisticViewAdapter adapter;

    class onDialogFinishHandler implements CustomDialog.onGetCardDialogFinish{

        Collection<Card> addedCards = null;
        @Override
        public void addCards(Collection<Card> addedCards){
            this.addedCards = addedCards;
        }

        @Override
        public void run() {
            Card card = addedCards.iterator().next();
            adapter = new StatisticViewAdapter(null);

            if (card == null)
                return;

            TexasModuleActivity.this.setViewActive(cardImages.get(TexasModuleActivity.this.getUsedCardCount()), card);
            deck.remove(card);

            if (TexasModuleActivity.this.getUsedCardCount() < 2)
                hand.add(card);
            else
                table.add(card);
            if (TexasModuleActivity.this.getUsedCardCount() == 2) {
                // PREFLOP
            } else if (TexasModuleActivity.this.getUsedCardCount() == 5) {
                updateStatsView(StatisticsGenerator.getStatistics(hand, table, deck));
                //setStats(StatisticsGenerator.getStatistics(hand, table, deck));
            } else if (TexasModuleActivity.this.getUsedCardCount() == 6) {
                //updateStatsView(Arrays.asList(deck.toArray()));
                //setStats(StatisticsGenerator.getStatistics(hand, table, deck));
            }
            if (TexasModuleActivity.this.getUsedCardCount() == 7) {
                addButton.setClickable(false);
                //setStats(StatisticsGenerator.getStatistics(hand, table, deck));
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Deck.initializeCardsImagesIds(this);
        setContentView(R.layout.activity_texas_module_constraint);
        cardImages.add((ImageView) findViewById(R.id.cardImageView0));
        cardImages.add((ImageView) findViewById(R.id.cardImageView1));
        cardImages.add((ImageView)findViewById(R.id.cardImageView2));
        cardImages.add((ImageView)findViewById(R.id.cardImageView3));
        cardImages.add((ImageView)findViewById(R.id.cardImageView4));
        cardImages.add((ImageView)findViewById(R.id.cardImageView5));
        cardImages.add((ImageView)findViewById(R.id.cardImageView6));
        //statsListView = findViewById(R.id.statsListView);
        addButton = findViewById(R.id.addCardButton);
        recyclerView = findViewById(R.id.recyclerView);

        addButton.setOnClickListener(v -> {
            CustomDialog dialog = new CustomDialog(TexasModuleActivity.this);
            AlertDialog alertDialog = dialog.showDialog(1, deck, new onDialogFinishHandler());
            alertDialog.show();
        });
    }

    private int getUsedCardCount(){
        return hand.size() + table.size();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_texas_module, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setViewActive(ImageView cardView, Card card){
        cardView.setImageResource(Deck.getCardImageId(card));
    }

    private void updateStatsView(List<Statistics> listOfStats){
        IconData[] data = new IconData[listOfStats.size()];
        for(int i = 0; i < listOfStats.size(); i++) {
            data[i] = new IconData(listOfStats.get(i).getUsedCards(),
                    listOfStats.get(i).getFigure(),
                    listOfStats.get(i).getChanceOfWinning(),
                    listOfStats.get(i).getChanceOfGetting());
        }

        adapter.updateData(data);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(TexasModuleActivity.this));
        recyclerView.setAdapter(adapter);
    }

}
