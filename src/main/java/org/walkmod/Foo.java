package no.finntech.finnbox.api.web.model;

import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import no.finntech.finnbox.model.domain.Pagination;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.schibsted.messaging.domain.obfuscation.MessageUriObfuscatorDeterministicAES;
import com.schibsted.messaging.service.inbox.InboxOverview;
import com.schibsted.messaging.service.inbox.Overview;

@Component
public class Foo extends AbstractAtomConverter {

  private final static MessageUriObfuscatorDeterministicAES obfuscator =
      new MessageUriObfuscatorDeterministicAES();

  private final AtomIdGenerator atomIdGenerator = new AtomIdGenerator();


  @Autowired
  public AtomInboxOverviewConverter(final UrlHelper urlHelper,
      final AtomMessageConverter atomMessageConverter) {
    super(urlHelper, "conversation/", "message/");
  }

  public AtomInboxOverviewConverter(final UrlHelper urlHelper,
      final AtomMessageConverter atomMessageConverter, final String conversationPath,
      final String relativeUrlPrefix) {
    super(urlHelper, conversationPath, relativeUrlPrefix);
  }


  /**
   * converts a list of conversations to an Atom feed element containing one entry for each
   * conversations. The Atom feed contains no messages!
   *
   * @param userId of the user owning the conversations
   * @param conversations to convert
   * @param path containing the conversations
   * @param pagination
   * @return
   */
  public Feed conversationsToAtomFeedElement(final String userId,
      final List<InboxOverview> overviews, final String path, Pagination pagination) {
    NavigationContext<InboxOverview> navContext =
        new NavigationContext<>(pagination, overviews.size());

    final Feed feed = getAbdera().newFeed();
    addLink(feed, userId, conversationPath + path, Link.REL_SELF, APPLICATION_ATOM_XML);
    feed.setId(atomIdGenerator.getId(path));
    feed.setTitle(path);
    feed.setUpdated(new Date(overviews.get(0).getOverview().getLastMessageDate()));
    addCategory("navigation:context:prev", String.valueOf(navContext.isPrev()), feed);
    addCategory("navigation:context:next", String.valueOf(navContext.isNext()), feed);

    List<InboxOverview> overviewsWithoutExtraItem = navContext.withoutExtraItems(overviews);
    for (InboxOverview inboxOverview : overviewsWithoutExtraItem) {
      Overview overview = inboxOverview.getOverview();
      final Entry entry = getAbdera().newEntry();

      String encryptedConvUri = obfuscator.obfuscate(inboxOverview.getLastMessageUri());
      entry.setId(atomIdGenerator.getId(encryptedConvUri));
      addIdentifier(encryptedConvUri, entry);
      addItemRelation(inboxOverview, entry);

      entry.setTitle(removeAsciiCtrlChars(overview.getTitle()));
      entry.setSummary("", Text.Type.TEXT);
      entry.setPublished(new Date(overview.getLastMessageDate()));
      entry.setUpdated(new Date(overview.getLastMessageDate()));
      addLink(entry, userId, conversationPath + encryptedConvUri, "item", APPLICATION_ATOM_XML);
      addCategory("conversation:folder", "inbox", entry);
      entry.addAuthor(overview.getPartnerName());

      // Custom finnbox fields
      addCustomFinboxElement("conversationPartner", overview.getPartnerName(), entry);
      addCustomFinboxElement("numberOfUnreadMessages", String.valueOf(overview.getUnseenCounter()),
          entry);

      feed.addEntry(entry);
    }
    return feed;
  }

  private final void addItemRelation(final InboxOverview overview, final ExtensibleElement parent) {
    final ExtensibleElement extensibleElement =
        parent.addExtension(new QName(EXT_DC, "relation", "dc"));
    extensibleElement.setText(overview.getKey().getItem().getType() + ":" + overview.getKey().getItem().getId());
  }

}
