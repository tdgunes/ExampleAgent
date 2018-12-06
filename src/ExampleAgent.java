import java.util.List;

import genius.core.AgentID;
import genius.core.Bid;
import genius.core.actions.Accept;
import genius.core.actions.Action;
import genius.core.actions.Offer;
import genius.core.parties.AbstractNegotiationParty;
import genius.core.parties.NegotiationInfo;


public class ExampleAgent extends AbstractNegotiationParty {
    private final String description = "Example Agent 3";

    private Bid lastReceivedOffer; // offer on the table
    private Bid myLastOffer;
    private List<Bid> bids; // TODO: not initialized

    @Override
    public void init(NegotiationInfo info) {
        super.init(info);

        // TODO: Fix bug here, NullPointerException
        bids.contains(myLastOffer);
    }

    @Override
    public Action chooseAction(List<Class<? extends Action>> list) {
        return new Accept(this.getPartyId(), lastReceivedOffer);
    }

    @Override
    public void receiveMessage(AgentID sender, Action act) {
        super.receiveMessage(sender, act);

        if (act instanceof Offer) { // sender is making an offer
            Offer offer = (Offer) act;

            // storing last received offer
            lastReceivedOffer = offer.getBid();
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    private Bid getMaxUtilityBid() {
        try {
            return this.utilitySpace.getMaxUtilityBid();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
