import javax.swing.ImageIcon;

public class NyawaCard implements ICard{
     String cardName;
    ImageIcon cardImageIcon;

    public NyawaCard(ImageIcon icon) {
        this.cardName = "nyawa";
        this.cardImageIcon = icon;
    }

    public String getCardName() { return cardName; }
    public ImageIcon getCardImageIcon() { return cardImageIcon; }
}
