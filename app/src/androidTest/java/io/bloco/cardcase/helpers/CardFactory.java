package io.bloco.cardcase.helpers;

import android.support.test.rule.ActivityTestRule;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;

import io.bloco.cardcase.AndroidApplication;
import io.bloco.cardcase.common.di.ApplicationModule;
import io.bloco.cardcase.data.models.Card;
import io.bloco.cardcase.data.models.Category;
import io.bloco.faker.Faker;

import java.sql.SQLException;
import java.util.UUID;

public class CardFactory {

    private final Faker faker;
    private RuntimeExceptionDao<Card, UUID> cardDao;
    private RuntimeExceptionDao<Category, UUID> categoryDao;

    public CardFactory() {
        faker = new Faker();
    }

    public CardFactory(ActivityTestRule activityTestRule) {
        this((AndroidApplication) activityTestRule.getActivity().getApplication());
    }

    public CardFactory(AndroidApplication application) {
        this();
        this.cardDao = new ApplicationModule(application).provideCardDao();
        this.categoryDao = new ApplicationModule(application).provideCategoryDao();
    }

    public CardFactory(RuntimeExceptionDao<Card, UUID> cardDao, RuntimeExceptionDao<Category, UUID> categoryDao) {
        this();
        this.cardDao = cardDao;
        this.categoryDao = categoryDao;

    }

    public Category buildCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return create(category);
    }

    public void updateCard(Card card) {
        cardDao.update(card);
    }

    public Card build() {
        Card card = new Card();
        card.setId(UUID.randomUUID());
        card.setName(faker.name.name());
        card.setCompany(faker.company.name());
        card.setAddress(faker.address.toString());
        card.setWebsite(faker.internet.email());
        card.setPosition(faker.company.profession());
        card.setEmail(faker.internet.email(card.getName()));
        card.setPhone(faker.phoneNumber.phoneNumber());
        card.setCreatedAt(faker.time.backward(365));
        card.setUpdatedAt(card.getCreatedAt());
        return card;
    }

    public Card buildUserCard() {
        Card card = build();
        card.setIsUser(true);
        return card;
    }

    public Card buildReceivedCard() {
        Card card = build();
        card.setIsUser(false);
        return card;
    }

    public Card create() {
        return create(build());
    }

    public Card createUserCard() {
        return create(buildUserCard());
    }

    public Card createReceivedCard() {
        return create(buildReceivedCard());
    }

    private Card create(final Card card) {
        cardDao.createOrUpdate(card);
        return card;
    }

    private Category create(final Category category) {
        categoryDao.createOrUpdate(category);
        return category;
    }

    public void clearCategories() {
        try {
            TableUtils.clearTable(categoryDao.getConnectionSource(), Category.class);
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void clear() {
        try {
            TableUtils.clearTable(cardDao.getConnectionSource(), Card.class);
        } catch (SQLException exception) {
            throw new RuntimeException(exception);
        }
    }
}
