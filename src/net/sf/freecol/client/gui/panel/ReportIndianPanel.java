/**
 *  Copyright (C) 2002-2013   The FreeCol Team
 *
 *  This file is part of FreeCol.
 *
 *  FreeCol is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  FreeCol is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with FreeCol.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.freecol.client.gui.panel;

import java.awt.Image;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSeparator;

import net.miginfocom.swing.MigLayout;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.client.gui.ImageLibrary;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Ability;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.NationSummary;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.StringTemplate;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;


/**
 * This panel displays the Native Affairs Advisor.
 */
public final class ReportIndianPanel extends ReportPanel {

    private static final String[] headlines = new String[] {
        "Settlement",
        "mission",
        "report.indian.tension",
        "report.indian.skillTaught",
        "report.indian.mostHated",
        "report.indian.tradeInterests"
    };


    /**
     * The constructor that will add the items to this panel.
     *
     * @param freeColClient The <code>FreeColClient</code> for the game.
     */
    public ReportIndianPanel(FreeColClient freeColClient) {
        super(freeColClient, Messages.message("reportIndianAction.name"));

        Player player = getMyPlayer();
        reportPanel.setLayout(new MigLayout("wrap 6, fillx, insets 0", "[]20px[center]", "[top]"));
        boolean needsSeperator = false;
        for (Player opponent : getGame().getPlayers()) {
            if (opponent.isIndian() && !opponent.isDead() && player.hasContacted(opponent)) {
                if(needsSeperator == true) {
                    reportPanel.add(new JSeparator(JSeparator.HORIZONTAL), "newline 20, span, growx, wrap 20");
                }
                buildIndianAdvisorPanel(player, opponent);
                needsSeperator = true;
            }
        }
        scrollPane.getViewport().setOpaque(false);
        reportPanel.setOpaque(true);
        reportPanel.doLayout();
    }

    /**
     * Describe <code>buildIndianAdvisorPanel</code> method here.
     *
     * @param player a <code>Player</code> value
     * @param opponent a <code>Player</code> value
     */
    private void buildIndianAdvisorPanel(Player player, Player opponent) {
        final NationSummary ns = getController().getNationSummary(opponent);
        List<IndianSettlement> nativeSettlements
            = opponent.getIndianSettlements();
        String numSettlements = "" + nativeSettlements.size() + " / "
            + ns.getNumberOfSettlements();

        ImageLibrary lib = getLibrary();
        JLabel villageLabel = new JLabel();
        villageLabel.setIcon(new ImageIcon(lib.getSettlementImage(opponent.getNationType().getCapitalType(), 0.66)));
        reportPanel.add(villageLabel, "span, split 2");
        JLabel headline = localizedLabel(opponent.getNationName());
        headline.setFont(smallHeaderFont);
        reportPanel.add(headline, "wrap 20");
        JLabel label = localizedLabel("report.indian.chieftain");
        label.setFont(boldFont);
        reportPanel.add(label);
        reportPanel.add(new JLabel(Messages.message(opponent.getName())), "left, wrap");
        label = localizedLabel("report.indian.typeOfSettlements");
        label.setFont(boldFont);
        reportPanel.add(label);
        reportPanel.add(localizedLabel(opponent.getNationType().getCapitalType().getId() + ".name"), "left, wrap");
        label = localizedLabel("report.indian.numberOfSettlements");
        reportPanel.add(label);
        label.setFont(boldFont);
        reportPanel.add(new JLabel(numSettlements), "left, wrap");
        label = localizedLabel("report.indian.tribeTension");
        reportPanel.add(label);
        label.setFont(boldFont);
        reportPanel.add(new JLabel(Messages.message(opponent.getTension(player).getKey())
                + "/" + Messages.message(opponent.getStance(player).getKey())),
            "left, wrap 20");

        if (!nativeSettlements.isEmpty()) {
            for (String key : headlines) {
                JLabel head = localizedLabel(key);
                head.setFont(boldFont);
                reportPanel.add(head);
            }
            List<IndianSettlement> settlements
                = new ArrayList<IndianSettlement>(nativeSettlements.size());
            for (IndianSettlement settlement : nativeSettlements) {
                if (settlement.isCapital()) {
                    settlements.add(0, settlement);
                } else {
                    settlements.add(settlement);
                }
            }
            for (IndianSettlement settlement : settlements) {
                boolean known = settlement.getTile().isExplored();
                boolean contacted = settlement.hasContacted(player);
                boolean visited = settlement.hasVisited(player);
                String locationName = Messages.message(settlement.getLocationNameFor(player));
                if (known && settlement.isCapital()) {
                    locationName += "*";
                }
                JButton settlementButton = getLinkButton(locationName, null,
                    settlement.getTile().getId());
                settlementButton.addActionListener(this);
                reportPanel.add(settlementButton, "newline 15");

                JLabel missionLabel = new JLabel();
                Unit missionary = settlement.getMissionary();
                if (missionary == null) {
                    missionLabel.setText("");
                } else {
                    boolean expert
                        = missionary.hasAbility(Ability.EXPERT_MISSIONARY);
                    Image chip = lib.getMissionChip(missionary.getOwner(),
                                                    expert);
                    missionLabel.setIcon(new ImageIcon(chip));
                    String text = Messages.message(StringTemplate
                        .template("model.unit.nationUnit")
                            .addStringTemplate("%nation%",
                                missionary.getOwner().getNationName())
                            .addStringTemplate("%unit%",
                                missionary.getFullLabel()));
                    missionLabel.setToolTipText(text);
                }
                reportPanel.add(missionLabel);

                String messageId = settlement.getShortAlarmLevelMessageId(player);
                reportPanel.add(localizedLabel(messageId));

                JLabel skillLabel = new JLabel();
                skillLabel.setVerticalTextPosition(JLabel.TOP);
                skillLabel.setHorizontalTextPosition(JLabel.CENTER);
                UnitType skillType = settlement.getLearnableSkill();
                String skillString;
                if (visited) {
                    if (skillType == null) {
                        skillString = "indianSettlement.skillNone";
                    } else {
                        skillString = skillType.getNameKey();
                        ImageIcon skillImage = getLibrary().getUnitImageIcon(skillType, 0.66);
                        skillLabel.setIcon(skillImage);
                    }
                } else {
                    skillString = "indianSettlement.skillUnknown";
                }
                skillLabel.setText(Messages.message(skillString));
                reportPanel.add(skillLabel);

                JLabel mostHatedLabel;
                Player mostHated = settlement.getMostHated();
                if (contacted) {
                    if (mostHated == null) {
                        mostHatedLabel = localizedLabel("indianSettlement.mostHatedNone");
                    } else {
                        mostHatedLabel = localizedLabel(mostHated.getNationName());
                    }
                } else {
                    mostHatedLabel = localizedLabel("indianSettlement.mostHatedUnknown");
                }
                reportPanel.add(mostHatedLabel);

                GoodsType[] wantedGoods = settlement.getWantedGoods();
                if (contacted) {
                    if (wantedGoods[0] == null) {
                        reportPanel.add(localizedLabel("indianSettlement.wantedGoodsNone"));
                    } else {
                        JLabel goodsLabel = localizedLabel(wantedGoods[0].getNameKey());
                        goodsLabel.setIcon(new ImageIcon(getLibrary().getGoodsImage(wantedGoods[0], 0.66)));
                        String split = "flowy, split " + String.valueOf(wantedGoods.length);
                        reportPanel.add(goodsLabel, split);
                        for (int i = 1; i < wantedGoods.length; i++) {
                            if (wantedGoods[i] != null) {
                                String sale = player.getLastSaleString(settlement, wantedGoods[i]);
                                goodsLabel = new JLabel(Messages.message(wantedGoods[i].getNameKey())
                                    + ((sale == null) ? "" : " " + sale));
                                goodsLabel.setIcon(getLibrary().getScaledGoodsImageIcon(wantedGoods[i], 0.5));
                                reportPanel.add(goodsLabel);
                            }
                        }
                    }
                } else {
                    reportPanel.add(localizedLabel("indianSettlement.wantedGoodsUnknown"));
                }
            }
        } else {
            reportPanel.add(localizedLabel("report.indian.noKnownSettlements"));
        }
    }
}
