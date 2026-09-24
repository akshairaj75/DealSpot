package com.backend.dealspot.service;

import com.backend.dealspot.dto.offer.OfferPeriodSplitRequestDto;
import com.backend.dealspot.dto.offer.OfferRequestDto;
import com.backend.dealspot.dto.offer.OfferResponseDto;
import com.backend.dealspot.entity.*;
import com.backend.dealspot.enums.AccountType;
import com.backend.dealspot.enums.AdminRole;
import com.backend.dealspot.exception.OfferConflictException;
import com.backend.dealspot.repository.*;
import com.backend.dealspot.security.CustomUserPrincipal;
import com.backend.dealspot.serviceImpl.FileStorageService;
import com.backend.dealspot.serviceImpl.OfferServiceImpl;
import com.backend.dealspot.serviceImpl.SpecialOfferServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpecialOfferSplittingAndConflictTest {

    @Mock
    private StoreRepository storeRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CityRepository cityRepository;
    @Mock
    private OfferRepository offerRepository;
    @Mock
    private SpecialOfferRepository specialOfferRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private OfferImageRepository offerImageRepository;
    @Mock
    private AuditLogService auditLogService;

    private OfferServiceImpl offerService;
    private SpecialOfferServiceImpl specialOfferService;

    private CustomUserPrincipal adminUser;
    private MockHttpServletRequest httpRequest;

    private Product product;
    private Store store;
    private City city;
    private Category category;
    private SpecialOffer specialOffer;

    @BeforeEach
    void setUp() {
        offerService = new OfferServiceImpl(
                storeRepository,
                productRepository,
                categoryRepository,
                cityRepository,
                offerRepository,
                specialOfferRepository,
                fileStorageService,
                offerImageRepository,
                auditLogService
        );

        specialOfferService = new SpecialOfferServiceImpl(
                specialOfferRepository,
                offerRepository,
                storeRepository,
                cityRepository,
                fileStorageService,
                auditLogService,
                offerService
        );

        adminUser = new CustomUserPrincipal(
                1L,
                "admin@dealspot.com",
                "secret",
                AccountType.ADMIN,
                AdminRole.SUPER_ADMIN,
                true,
                Collections.emptyList()
        );

        httpRequest = new MockHttpServletRequest();

        city = new City();
        city.setId(1);
        city.setNameEn("Riyadh");

        store = new Store();
        store.setId(10);
        store.setNameEn("HyperPanda");
        store.setCity(city);

        category = new Category();
        category.setId(5);
        category.setNameEn("Electronics");

        product = new Product();
        product.setId(100L);
        product.setNameEn("Samsung Galaxy S24");
        product.setCategory(category);

        specialOffer = new SpecialOffer();
        specialOffer.setId(500L);
        specialOffer.setTitleEn("Eid Mega Sale");
        specialOffer.setActive(true);
        specialOffer.setValidFrom(LocalDate.of(2026, 6, 1));
        specialOffer.setValidUntil(LocalDate.of(2026, 6, 30));
        specialOffer.setOffers(new ArrayList<>());
    }

    @Test
    @DisplayName("Normal addOffer: throws 409 OfferConflictException when date overlaps exist")
    void addOffer_withOverlap_throwsOfferConflictException() {
        OfferRequestDto dto = new OfferRequestDto();
        dto.setProductId(100L);
        dto.setStoreId(10L);
        dto.setCategoryId(5L);
        dto.setCityId(1L);
        dto.setValidFrom(LocalDate.of(2026, 6, 1));
        dto.setValidUntil(LocalDate.of(2026, 6, 15));
        dto.setOfferPrice(new BigDecimal("299.00"));
        dto.setOriginalPrice(new BigDecimal("399.00"));
        dto.setTitleEn("Summer Deal");

        Offer existingOffer = new Offer();
        existingOffer.setId(1L);
        existingOffer.setTitleEn("Existing Summer Deal");
        existingOffer.setTitleAr("صفقة الصيف");
        existingOffer.setOfferPrice(new BigDecimal("320.00"));
        existingOffer.setOriginalPrice(new BigDecimal("399.00"));
        existingOffer.setValidFrom(LocalDate.of(2026, 6, 10));
        existingOffer.setValidUntil(LocalDate.of(2026, 6, 25));
        existingOffer.setProduct(product);
        existingOffer.setStore(store);

        when(storeRepository.findById(10)).thenReturn(Optional.of(store));
        when(cityRepository.findById(1)).thenReturn(Optional.of(city));
        when(categoryRepository.findById(5)).thenReturn(Optional.of(category));
        when(productRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(product));
        when(offerRepository.findOverlappingActiveOffers(eq(100L), eq(10), eq(dto.getValidFrom()), eq(dto.getValidUntil())))
                .thenReturn(List.of(existingOffer));

        OfferConflictException ex = assertThrows(OfferConflictException.class, () ->
                offerService.addOffer(dto, null, adminUser, httpRequest)
        );

        assertEquals(1L, ex.getConflictingOfferId());
        assertEquals(100L, ex.getProductId());
        assertEquals(10, ex.getStoreId());
    }

    @Test
    @DisplayName("Normal addOffer: succeeds when no active overlap exists")
    void addOffer_withoutOverlap_success() {
        OfferRequestDto dto = new OfferRequestDto();
        dto.setProductId(100L);
        dto.setStoreId(10L);
        dto.setCategoryId(5L);
        dto.setCityId(1L);
        dto.setValidFrom(LocalDate.of(2026, 7, 1));
        dto.setValidUntil(LocalDate.of(2026, 7, 31));
        dto.setOfferPrice(new BigDecimal("299.00"));
        dto.setOriginalPrice(new BigDecimal("399.00"));
        dto.setTitleEn("July Deal");
        dto.setTitleAr("صفقة يوليو");

        when(storeRepository.findById(10)).thenReturn(Optional.of(store));
        when(cityRepository.findById(1)).thenReturn(Optional.of(city));
        when(categoryRepository.findById(5)).thenReturn(Optional.of(category));
        when(productRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(product));
        when(offerRepository.findOverlappingActiveOffers(eq(100L), eq(10), eq(dto.getValidFrom()), eq(dto.getValidUntil())))
                .thenReturn(Collections.emptyList());
        when(offerRepository.save(any(Offer.class))).thenAnswer(invocation -> {
            Offer saved = invocation.getArgument(0);
            saved.setId(20L);
            return saved;
        });

        OfferResponseDto response = offerService.addOffer(dto, null, adminUser, httpRequest);

        assertNotNull(response);
        assertEquals(20L, response.getId());
        assertEquals("July Deal", response.getTitleEn());
    }

    @Test
    @DisplayName("updateOffer: ignores current offer in overlap check and succeeds")
    void updateOffer_sameDatesExcludingSelf_success() {
        Long offerId = 15L;
        Offer existing = new Offer();
        existing.setId(offerId);
        existing.setProduct(product);
        existing.setStore(store);
        existing.setCategory(category);
        existing.setCity(city);
        existing.setValidFrom(LocalDate.of(2026, 6, 1));
        existing.setValidUntil(LocalDate.of(2026, 6, 30));
        existing.setTitleEn("June Deal");
        existing.setTitleAr("صفقة يونيو");
        existing.setOfferPrice(new BigDecimal("100.00"));
        existing.setOriginalPrice(new BigDecimal("150.00"));

        OfferRequestDto dto = new OfferRequestDto();
        dto.setProductId(100L);
        dto.setStoreId(10L);
        dto.setCategoryId(5L);
        dto.setCityId(1L);
        dto.setValidFrom(LocalDate.of(2026, 6, 1));
        dto.setValidUntil(LocalDate.of(2026, 6, 30));
        dto.setTitleEn("June Deal Updated");
        dto.setTitleAr("صفقة يونيو المحدثة");
        dto.setOfferPrice(new BigDecimal("95.00"));
        dto.setOriginalPrice(new BigDecimal("150.00"));

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(existing));
        when(productRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(product));
        when(offerRepository.findOverlappingActiveOffersExcluding(eq(100L), eq(10), eq(dto.getValidFrom()), eq(dto.getValidUntil()), eq(offerId)))
                .thenReturn(Collections.emptyList());
        when(offerRepository.save(any(Offer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OfferResponseDto result = offerService.updateOffer(offerId, dto, null, adminUser, httpRequest);
        assertNotNull(result);
        assertEquals("June Deal Updated", result.getTitleEn());
        assertEquals(new BigDecimal("95.00"), result.getOfferPrice());
    }

    @Test
    @DisplayName("extendOffer: throws 409 OfferConflictException if extended window overlaps another offer")
    void extendOffer_withOverlap_throwsOfferConflictException() {
        Long offerId = 15L;
        LocalDate now = LocalDate.now();
        LocalDate futureEnd = now.plusDays(10);
        LocalDate newValidUntil = futureEnd.plusDays(10);

        Offer current = new Offer();
        current.setId(offerId);
        current.setProduct(product);
        current.setStore(store);
        current.setValidFrom(now.minusDays(5));
        current.setValidUntil(futureEnd);
        current.setActive(true);

        Offer conflicting = new Offer();
        conflicting.setId(99L);
        conflicting.setTitleEn("Promo");
        conflicting.setProduct(product);
        conflicting.setStore(store);
        conflicting.setValidFrom(futureEnd.plusDays(1));
        conflicting.setValidUntil(futureEnd.plusDays(20));

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(current));
        when(productRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(product));
        when(offerRepository.findOverlappingActiveOffersExcluding(eq(100L), eq(10), eq(current.getValidFrom()), eq(newValidUntil), eq(offerId)))
                .thenReturn(List.of(conflicting));

        assertThrows(OfferConflictException.class, () ->
                offerService.extendOffer(offerId, 10, adminUser)
        );
    }

    @Test
    @DisplayName("splitAndCreateOffer: Case 1 - Middle split generates truncated original, campaign offer, and remainder offer")
    void splitAndCreateOffer_middleSplit_success() {
        // Original: June 1 to Dec 31
        Offer original = new Offer();
        original.setId(50L);
        original.setTitleEn("6 Month Baseline Deal");
        original.setTitleAr("صفقة خط الأساس 6 أشهر");
        original.setProduct(product);
        original.setStore(store);
        original.setCategory(category);
        original.setCity(city);
        original.setValidFrom(LocalDate.of(2026, 6, 1));
        original.setValidUntil(LocalDate.of(2026, 12, 31));
        original.setOfferPrice(new BigDecimal("300.00"));
        original.setOriginalPrice(new BigDecimal("400.00"));
        original.setActive(true);

        OfferImage img1 = new OfferImage();
        img1.setId(101L);
        img1.setImageUrl("/uploads/offers/img1.jpg");
        img1.setOffer(original);
        original.getImages().add(img1);

        // Campaign: Aug 1 to Aug 10 at 250.00
        OfferPeriodSplitRequestDto dto = new OfferPeriodSplitRequestDto();
        dto.setExistingOfferId(50L);
        dto.setSpecialOfferId(500L);
        dto.setSplitValidFrom(LocalDate.of(2026, 8, 1));
        dto.setSplitValidUntil(LocalDate.of(2026, 8, 10));
        dto.setNewOfferPrice(new BigDecimal("250.00"));
        dto.setNewOriginalPrice(new BigDecimal("400.00"));
        dto.setTitleEn("Eid Special Deal");

        when(offerRepository.findById(50L)).thenReturn(Optional.of(original));
        when(productRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(product));
        when(specialOfferRepository.findById(500L)).thenReturn(Optional.of(specialOffer));

        List<Offer> savedOffers = new ArrayList<>();
        when(offerRepository.save(any(Offer.class))).thenAnswer(inv -> {
            Offer o = inv.getArgument(0);
            if (o.getId() == null) {
                o.setId((long) (1000 + savedOffers.size()));
            }
            savedOffers.add(o);
            return o;
        });

        OfferResponseDto campaignDto = offerService.splitAndCreateOffer(dto, adminUser, httpRequest);

        assertNotNull(campaignDto);
        assertEquals(new BigDecimal("250.00"), campaignDto.getOfferPrice());
        assertEquals(500L, campaignDto.getSpecialOfferId());
        assertEquals(LocalDate.of(2026, 8, 1), campaignDto.getValidFrom());
        assertEquals(LocalDate.of(2026, 8, 10), campaignDto.getValidUntil());

        // Verify original offer was truncated to July 31
        assertEquals(LocalDate.of(2026, 6, 1), original.getValidFrom());
        assertEquals(LocalDate.of(2026, 7, 31), original.getValidUntil());
        assertTrue(original.isActive());

        // Verify remainder offer was created for Aug 11 to Dec 31
        boolean foundRemainder = savedOffers.stream().anyMatch(o ->
                LocalDate.of(2026, 8, 11).equals(o.getValidFrom()) &&
                LocalDate.of(2026, 12, 31).equals(o.getValidUntil()) &&
                new BigDecimal("300.00").equals(o.getOfferPrice()) &&
                o.getSpecialOffer() == null
        );
        assertTrue(foundRemainder, "Remainder offer from Aug 11 to Dec 31 should have been saved");

        // Verify image copying to remainder offer
        verify(offerImageRepository, atLeastOnce()).save(any(OfferImage.class));
    }

    @Test
    @DisplayName("splitAndCreateOffer: Case 2 - Start split shifts original offer start forward")
    void splitAndCreateOffer_startSplit_success() {
        // Original: June 1 to Dec 31
        Offer original = new Offer();
        original.setId(60L);
        original.setTitleEn("Baseline Deal");
        original.setProduct(product);
        original.setStore(store);
        original.setCategory(category);
        original.setCity(city);
        original.setValidFrom(LocalDate.of(2026, 6, 1));
        original.setValidUntil(LocalDate.of(2026, 12, 31));
        original.setOfferPrice(new BigDecimal("300.00"));
        original.setOriginalPrice(new BigDecimal("400.00"));
        original.setActive(true);

        // Campaign: June 1 to June 10
        OfferPeriodSplitRequestDto dto = new OfferPeriodSplitRequestDto();
        dto.setExistingOfferId(60L);
        dto.setSpecialOfferId(500L);
        dto.setSplitValidFrom(LocalDate.of(2026, 6, 1));
        dto.setSplitValidUntil(LocalDate.of(2026, 6, 10));
        dto.setNewOfferPrice(new BigDecimal("220.00"));

        when(offerRepository.findById(60L)).thenReturn(Optional.of(original));
        when(productRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(product));
        when(specialOfferRepository.findById(500L)).thenReturn(Optional.of(specialOffer));
        when(offerRepository.save(any(Offer.class))).thenAnswer(inv -> {
            Offer o = inv.getArgument(0);
            if (o.getId() == null) o.setId(999L);
            return o;
        });

        OfferResponseDto campaignDto = offerService.splitAndCreateOffer(dto, adminUser, httpRequest);

        assertNotNull(campaignDto);
        assertEquals(LocalDate.of(2026, 6, 1), campaignDto.getValidFrom());
        assertEquals(LocalDate.of(2026, 6, 10), campaignDto.getValidUntil());

        // Original shifted forward to start June 11
        assertEquals(LocalDate.of(2026, 6, 11), original.getValidFrom());
        assertEquals(LocalDate.of(2026, 12, 31), original.getValidUntil());
    }

    @Test
    @DisplayName("splitAndCreateOffer: Case 3 - End split truncates original offer end date")
    void splitAndCreateOffer_endSplit_success() {
        // Original: June 1 to Dec 31
        Offer original = new Offer();
        original.setId(70L);
        original.setTitleEn("Baseline Deal");
        original.setProduct(product);
        original.setStore(store);
        original.setCategory(category);
        original.setCity(city);
        original.setValidFrom(LocalDate.of(2026, 6, 1));
        original.setValidUntil(LocalDate.of(2026, 12, 31));
        original.setOfferPrice(new BigDecimal("300.00"));
        original.setOriginalPrice(new BigDecimal("400.00"));
        original.setActive(true);

        // Campaign: Dec 20 to Dec 31
        OfferPeriodSplitRequestDto dto = new OfferPeriodSplitRequestDto();
        dto.setExistingOfferId(70L);
        dto.setSpecialOfferId(500L);
        dto.setSplitValidFrom(LocalDate.of(2026, 12, 20));
        dto.setSplitValidUntil(LocalDate.of(2026, 12, 31));
        dto.setNewOfferPrice(new BigDecimal("199.00"));

        when(offerRepository.findById(70L)).thenReturn(Optional.of(original));
        when(productRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(product));
        when(specialOfferRepository.findById(500L)).thenReturn(Optional.of(specialOffer));
        when(offerRepository.save(any(Offer.class))).thenAnswer(inv -> {
            Offer o = inv.getArgument(0);
            if (o.getId() == null) o.setId(998L);
            return o;
        });

        OfferResponseDto campaignDto = offerService.splitAndCreateOffer(dto, adminUser, httpRequest);

        assertNotNull(campaignDto);
        assertEquals(LocalDate.of(2026, 12, 20), campaignDto.getValidFrom());
        assertEquals(LocalDate.of(2026, 12, 31), campaignDto.getValidUntil());

        // Original truncated to Dec 19
        assertEquals(LocalDate.of(2026, 6, 1), original.getValidFrom());
        assertEquals(LocalDate.of(2026, 12, 19), original.getValidUntil());
    }

    @Test
    @DisplayName("splitAndCreateOffer: Case 4 - Exact date replacement deactivates original offer")
    void splitAndCreateOffer_exactReplacement_success() {
        // Original: June 1 to June 10
        Offer original = new Offer();
        original.setId(80L);
        original.setTitleEn("Ten Day Deal");
        original.setProduct(product);
        original.setStore(store);
        original.setCategory(category);
        original.setCity(city);
        original.setValidFrom(LocalDate.of(2026, 6, 1));
        original.setValidUntil(LocalDate.of(2026, 6, 10));
        original.setOfferPrice(new BigDecimal("300.00"));
        original.setOriginalPrice(new BigDecimal("400.00"));
        original.setActive(true);

        // Campaign: exact same range June 1 to June 10
        OfferPeriodSplitRequestDto dto = new OfferPeriodSplitRequestDto();
        dto.setExistingOfferId(80L);
        dto.setSpecialOfferId(500L);
        dto.setSplitValidFrom(LocalDate.of(2026, 6, 1));
        dto.setSplitValidUntil(LocalDate.of(2026, 6, 10));
        dto.setNewOfferPrice(new BigDecimal("180.00"));

        when(offerRepository.findById(80L)).thenReturn(Optional.of(original));
        when(productRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(product));
        when(specialOfferRepository.findById(500L)).thenReturn(Optional.of(specialOffer));
        when(offerRepository.save(any(Offer.class))).thenAnswer(inv -> {
            Offer o = inv.getArgument(0);
            if (o.getId() == null) o.setId(997L);
            return o;
        });

        OfferResponseDto campaignDto = offerService.splitAndCreateOffer(dto, adminUser, httpRequest);

        assertNotNull(campaignDto);
        assertFalse(original.isActive(), "Original offer must be deactivated on exact range replacement");
    }

    @Test
    @DisplayName("splitAndCreateOffer: throws IllegalArgumentException if split dates fall outside original offer window")
    void splitAndCreateOffer_outOfBounds_throwsIllegalArgumentException() {
        Offer original = new Offer();
        original.setId(90L);
        original.setProduct(product);
        original.setStore(store);
        original.setValidFrom(LocalDate.of(2026, 6, 1));
        original.setValidUntil(LocalDate.of(2026, 6, 30));

        OfferPeriodSplitRequestDto dto = new OfferPeriodSplitRequestDto();
        dto.setExistingOfferId(90L);
        dto.setSplitValidFrom(LocalDate.of(2026, 5, 20)); // Before June 1
        dto.setSplitValidUntil(LocalDate.of(2026, 6, 10));

        when(offerRepository.findById(90L)).thenReturn(Optional.of(original));
        when(productRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () ->
                offerService.splitAndCreateOffer(dto, adminUser, httpRequest)
        );
    }

    @Test
    @DisplayName("SpecialOfferService: deleteSpecialOffer detaches offers without deleting them")
    void deleteSpecialOffer_detachesOffersWithoutDeleting() {
        Offer offer1 = new Offer();
        offer1.setId(101L);
        offer1.setTitleEn("Offer 1");
        offer1.setSpecialOffer(specialOffer);
        offer1.setActive(true);

        Offer offer2 = new Offer();
        offer2.setId(102L);
        offer2.setTitleEn("Offer 2");
        offer2.setSpecialOffer(specialOffer);
        offer2.setActive(true);

        specialOffer.getOffers().add(offer1);
        specialOffer.getOffers().add(offer2);

        when(specialOfferRepository.findById(500L)).thenReturn(Optional.of(specialOffer));
        when(offerRepository.findBySpecialOfferId(500L)).thenReturn(List.of(offer1, offer2));

        specialOfferService.deleteSpecialOffer(500L, adminUser, httpRequest);

        // Verify offers are detached, not deleted
        assertNull(offer1.getSpecialOffer());
        assertNull(offer2.getSpecialOffer());
        verify(offerRepository).save(offer1);
        verify(offerRepository).save(offer2);
        verify(offerRepository, never()).delete(any(Offer.class));
        verify(specialOfferRepository).delete(specialOffer);
    }

    @Test
    @DisplayName("splitAndCreateOffer: modifying already-overridden campaign offer updates in-place on the same Offer ID")
    void splitAndCreateOffer_alreadyCampaignOffer_updatesInPlace_noNewOfferCreated() {
        Long offerId = 250L;
        Offer campaignOffer = new Offer();
        campaignOffer.setId(offerId);
        campaignOffer.setTitleEn("Eid Deal v1");
        campaignOffer.setTitleAr("عرض العيد");
        campaignOffer.setProduct(product);
        campaignOffer.setStore(store);
        campaignOffer.setCategory(category);
        campaignOffer.setCity(city);
        campaignOffer.setValidFrom(LocalDate.of(2026, 6, 1));
        campaignOffer.setValidUntil(LocalDate.of(2026, 6, 15));
        campaignOffer.setOfferPrice(new BigDecimal("199.00"));
        campaignOffer.setOriginalPrice(new BigDecimal("300.00"));
        campaignOffer.setSpecialOffer(specialOffer);
        campaignOffer.setActive(true);

        OfferPeriodSplitRequestDto dto = new OfferPeriodSplitRequestDto();
        dto.setExistingOfferId(offerId);
        dto.setSpecialOfferId(500L);
        dto.setSplitValidFrom(LocalDate.of(2026, 6, 1));
        dto.setSplitValidUntil(LocalDate.of(2026, 6, 15));
        dto.setNewOfferPrice(new BigDecimal("179.00"));
        dto.setNewOriginalPrice(new BigDecimal("300.00"));
        dto.setTitleEn("Eid Deal v2 (Adjusted Price)");

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(campaignOffer));
        when(productRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(product));
        when(specialOfferRepository.findById(500L)).thenReturn(Optional.of(specialOffer));
        when(offerRepository.findOverlappingActiveOffersExcluding(eq(100L), eq(10), eq(dto.getSplitValidFrom()), eq(dto.getSplitValidUntil()), eq(offerId)))
                .thenReturn(Collections.emptyList());
        when(offerRepository.save(any(Offer.class))).thenAnswer(inv -> inv.getArgument(0));

        OfferResponseDto result = offerService.splitAndCreateOffer(dto, adminUser, httpRequest);

        assertNotNull(result);
        assertEquals(offerId, result.getId(), "Must maintain the EXACT same Offer ID");
        assertEquals(new BigDecimal("179.00"), result.getOfferPrice(), "Price must be updated to 179.00");
        assertEquals("Eid Deal v2 (Adjusted Price)", result.getTitleEn());
        assertTrue(campaignOffer.isActive(), "Offer must remain active");
        assertEquals(500L, result.getSpecialOfferId());
    }
}
