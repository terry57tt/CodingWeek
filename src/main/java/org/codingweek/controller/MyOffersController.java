package org.codingweek.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.codingweek.ApplicationContext;
import org.codingweek.ApplicationSettings;
import org.codingweek.db.DatabaseManager;
import org.codingweek.db.entity.Offer;
import org.codingweek.db.entity.User;
import org.codingweek.model.DatabaseHandler;
import org.codingweek.model.ImageHandler;
import org.codingweek.model.MyOffersModel;
import org.codingweek.model.Page;
import org.codingweek.view.MyOffersView;
import org.codingweek.view.OfferCreateView;
import org.codingweek.view.OfferModalView;
import org.codingweek.view.OfferModifView;

import javax.swing.plaf.ButtonUI;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MyOffersController extends Controller implements Observeur {

    private List<Offer> myOffers = new ArrayList<>();

    private int indexModify = -1;

    @FXML
    public VBox scrollField;



    @Override
    public void refresh() {
        this.scrollField.getChildren().clear();
        Pane pane;
        int x = 0;
        HBox hBox = new HBox();

        for (Offer offer : this.myOffers) {
            pane = makePaneOffer(offer);
            hBox.getChildren().add(pane);
            x++;

            if (x == 2) {
                this.scrollField.getChildren().add(hBox);
                hBox = new HBox();
                x = 0;
            }
        }
        this.scrollField.getChildren().add(hBox);
    }

    @Override
    public void update() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ApplicationContext.getInstance().setPageType(Page.OFFER);
        this.myOffers.clear();
        User user = ApplicationContext.getInstance().getUser_authentified();
        this.myOffers = MyOffersModel.getMyOffers(user.getEmail());
        refresh();
    }

    public void sendToCreateOffer(ActionEvent actionEvent) {
        ApplicationContext.getInstance().setPageType(Page.OFFER);
        try {
            ApplicationSettings.getInstance().setCurrentScene(new OfferCreateView().loadScene());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Pane makePaneOffer(Offer offer) {
        ImageView image = new ImageView();
        image.setImage(ImageHandler.getImage(offer.getPath()));
        image.setTranslateX(20);
        image.setTranslateY(20);
        image.setFitHeight(200);
        image.setFitWidth(200);

        Label titleLabel = new Label(offer.getTitle());
        titleLabel.getStyleClass().add("title");
        titleLabel.setMaxWidth(150);

        Label priceLabel = new Label(offer.getPrice() + " florains");
        priceLabel.getStyleClass().add("price");

        String offerType;
        switch(offer.getTypeOffer()){
            case LOAN:
                offerType = "Prêt";
                break;
            case SERVICE:
                offerType = "Service";
                break;
            default:
                offerType = "";
        }
        Label offerTypeLabel = new Label(offerType);

        Label frequencyLabel = new Label(offer.getFrequency().getValue());

        Button consulter = new Button("Consulter");
        consulter.getStyleClass().add("buttonConsult");
        consulter.setOnAction(e -> {
            this.indexModify = offer.getId();
            showOfferModal(e);
        });

        Button modifier = new Button("Modifier");
        modifier.getStyleClass().add("buttonModOffer");
        modifier.setOnAction(e -> {
            this.indexModify = offer.getId();
            showOfferModif(e);
        });

        Button supprimer = new Button("Supprimer");
        supprimer.getStyleClass().add("buttonSupOffer");
        supprimer.setOnAction(e -> {
            this.indexModify = offer.getId();
            showConfirmationAddDialog(e);
        });

        VBox vbox = new VBox();
        vbox.getChildren().addAll(titleLabel, priceLabel, offerTypeLabel, frequencyLabel, consulter, modifier, supprimer);
        vbox.getStyleClass().add("margin");

        HBox hbox = new HBox();
        hbox.getChildren().addAll(image, vbox);

        Pane pane = new Pane();
        pane.getStyleClass().add("offer");
        pane.setMinSize(100, 100);
        pane.getChildren().add(hbox);
        return pane;
    }

    private void showOfferModif(ActionEvent event) {
        try {
            ApplicationContext.getInstance().setOfferId(this.indexModify);
            ApplicationSettings.getInstance().setCurrentScene(new OfferModifView().loadScene());
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    private void showOfferModal(ActionEvent event) {
        ApplicationContext.getInstance().setPageType(Page.OFFER);
        try {
            ApplicationSettings.getInstance().setCurrentScene(new OfferModalView().loadScene());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void modifyOffer(ActionEvent event) {
        ApplicationContext.getInstance().setPageType(Page.OFFER);
        try {
            ApplicationSettings.getInstance().setCurrentScene(new OfferModifView().loadScene());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showConfirmationAddDialog(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Etes vous sur ?");
        alert.setContentText("Voulez vous vraiment supprimer cette offre ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                DatabaseManager db = DatabaseHandler.getInstance().getDbManager();
                //db.deleteEntity(this.myOffers);
                ApplicationContext.getInstance().setPageType(Page.OFFER);
                try {
                    ApplicationSettings.getInstance().setCurrentScene(new MyOffersView().loadScene());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
