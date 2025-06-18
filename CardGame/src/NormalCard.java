import javax.swing.ImageIcon;

public class NormalCard implements ICard {
    String cardName;
    ImageIcon cardImageIcon;

    public NormalCard(String name, ImageIcon icon) {
        this.cardName = name;
        this.cardImageIcon = icon;
    }

    public String getCardName() { return cardName; }
    public ImageIcon getCardImageIcon() { return cardImageIcon; }
}
